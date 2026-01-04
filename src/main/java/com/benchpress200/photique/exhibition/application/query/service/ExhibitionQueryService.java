package com.benchpress200.photique.exhibition.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionWorkQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionQueryService implements
        GetExhibitionDetailsUseCase {
    private final AuthenticationUserProviderPort authenticationUserProvider;

    private final FollowQueryPort followQueryPort;

    private final ExhibitionCommandPort exhibitionCommandPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionTagQueryPort exhibitionTagQueryPort;
    private final ExhibitionWorkQueryPort exhibitionWorkQueryPort;
    private final ExhibitionLikeQueryPort exhibitionLikeQueryPort;
    private final ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Override
    public ExhibitionDetailsResult getExhibitionDetails(Long exhibitionId) {
        // 전시회 조회
        Exhibition exhibition = exhibitionQueryPort.findActiveByIdWithWriter(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 태그 조회
        List<ExhibitionTag> exhibitionTags = exhibitionTagQueryPort.findByExhibitionWithTag(exhibition);
        List<Tag> tags = exhibitionTags.stream()
                .map(ExhibitionTag::getTag)
                .toList();

        // 개별 작품 조회
        List<ExhibitionWork> exhibitionWorks = exhibitionWorkQueryPort.findByExhibition(exhibition);

        // 작가 팔로우, 좋아요, 북마크 유무 조회
        Long requestUserId = authenticationUserProvider.getCurrentUserId();
        Long writerId = exhibition.getWriter().getId();

        boolean isFollowing = followQueryPort.existsByFollowerIdAndFolloweeId(requestUserId, writerId);
        boolean isLiked = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(requestUserId, exhibitionId);
        boolean isBookmarked = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(requestUserId, exhibitionId);

        // 조회수 증가
        // FIXME: 현재 레코드 레벨에서 원자적인 업데이트를 위한 쿼리가 나가는 중인데,
        // FIXME: query 어노테이션 API 임에도 불구하고 쓰기 쿼리가 발생하는 트랜잭션임
        // FIXME: 이후에 JPA 쓰기 지연 VS 원자적 쿼리 수행 VS 비관적, 낙관적 락 VS 실시간 동기화 포기하고 레디스 활용 방안 비교하고 적용
        // FIXME: failover 로 기존 로직 동작?
        // FIXME: ES에 조회수 동기화도 필요 & 좋아요 수 동기화도 필요함
        exhibitionCommandPort.incrementViewCount(exhibitionId);

        return ExhibitionDetailsResult.of(
                exhibition,
                tags,
                exhibitionWorks,
                isFollowing,
                isLiked,
                isBookmarked
        );
    }
}
