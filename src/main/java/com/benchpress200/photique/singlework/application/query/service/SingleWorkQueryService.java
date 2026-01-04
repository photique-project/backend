package com.benchpress200.photique.singlework.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.query.model.MySingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMySingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.result.SearchedSingleWork;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.support.LikedSingleWorkIds;
import com.benchpress200.photique.singlework.application.query.support.SearchedSingleWorks;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkQueryService implements
        GetSingleWorkDetailsUseCase,
        SearchSingleWorkUseCase,
        SearchMySingleWorkUseCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    private final SingleWorkCommandPort singleWorkCommandPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkTagQueryPort singleWorkTagQueryPort;
    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    private final FollowQueryPort followQueryPort;

    public SingleWorkDetailsResult getSingleWorkDetails(Long singleWorkId) {
        // 작품 조회
        SingleWork singleWork = singleWorkQueryPort.findActiveByIdWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 태그 조회
        List<SingleWorkTag> singleWorkTags = singleWorkTagQueryPort.findBySingleWorkWithTag(singleWork);
        List<Tag> tags = singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();

        // 작품 좋아요 유무, 작가 팔로우 유무 확인
        boolean isLiked = false;
        boolean isFollowing = false;

        if (authenticationUserProviderPort.isAuthenticated()) {
            Long requestUserId = authenticationUserProviderPort.getCurrentUserId();
            Long writerId = singleWork.getWriter().getId();

            isLiked = singleWorkLikeQueryPort.existsByUserIdAndSingleWorkId(requestUserId, singleWorkId);
            isFollowing = followQueryPort.existsByFollowerIdAndFolloweeId(requestUserId, writerId);
        }

        // FIXME: 현재 레코드 레벨에서 원자적인 업데이트를 위한 쿼리가 나가는 중인데,
        // FIXME: query 어노테이션 API 임에도 불구하고 쓰기 쿼리가 발생하는 트랜잭션임
        // FIXME: 이후에 JPA 쓰기 지연 VS 원자적 쿼리 수행 VS 비관적, 낙관적 락 VS 실시간 동기화 포기하고 레디스 활용 방안 비교하고 적용
        // FIXME: failover 로 기존 로직 동작?
        // FIXME: ES에 조회수 동기화도 필요 & 좋아요 수 동기화도 필요함
        singleWorkCommandPort.incrementViewCount(singleWorkId);

        return SingleWorkDetailsResult.of(
                singleWork,
                tags,
                isLiked,
                isFollowing
        );
    }

    public SingleWorkSearchResult searchSingleWork(SingleWorkSearchQuery query) {
        Target target = query.getTarget();
        String keyword = query.getKeyword();
        List<Category> categories = query.getCategories();
        Pageable pageable = query.getPageable();

        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkQueryPort.search(
                target,
                keyword,
                categories,
                pageable
        );

        // 요청 유저가 좋아요한 작품 아이디 조회
        List<Long> singleWorkSearchIds = singleWorkSearchPage.stream()
                .map(SingleWorkSearch::getId)
                .toList();

        LikedSingleWorkIds likedSingleWorkIds = findLikedSingleWorkIds(singleWorkSearchIds);

        // 각 작품마다 유저의 좋아요 여부 확인
        SearchedSingleWorks searchedSingleWorks = SearchedSingleWorks.of(singleWorkSearchPage, likedSingleWorkIds);

        return SingleWorkSearchResult.of(searchedSingleWorks, singleWorkSearchPage);
    }

    @Override
    public MySingleWorkSearchResult searchMySingleWork(MySingleWorkSearchQuery query) {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        // 본인 단일작품 페이지 조회
        Page<SingleWork> singleWorkPage = singleWorkQueryPort.searchMySingleWork(
                userId,
                keyword,
                pageable
        );

        // 요청 유저가 좋아요한 작품 아이디 조회
        List<Long> singleWorkIds = singleWorkPage.stream()
                .map(SingleWork::getId)
                .toList();

        LikedSingleWorkIds likedSingleWorkIds = findLikedSingleWorkIds(singleWorkIds);

        // FIXME: 일급 컬렉션을 활용하여 간소화
        // 각 작품마다 유저의 좋아요 여부 확인
        List<SearchedSingleWork> searchedSingleWorks = singleWorkPage.stream()
                .map(singleWork -> {
                    Long singleWorkId = singleWork.getId();
                    boolean isLiked = likedSingleWorkIds.contains(singleWorkId);

                    return SearchedSingleWork.of(singleWork, isLiked);
                })
                .toList();

        return MySingleWorkSearchResult.of(searchedSingleWorks, singleWorkPage);
    }

    private LikedSingleWorkIds findLikedSingleWorkIds(List<Long> ids) {
        // 인증된 유저가 아니라면
        if (!authenticationUserProviderPort.isAuthenticated()) {
            return LikedSingleWorkIds.empty();
        }

        Long requestUserId = authenticationUserProviderPort.getCurrentUserId();

        // 검색 결과 단일작품 중에서 요청 유저가 좋아요한 작품 아이디 셋 조회
        Set<Long> likedSet = singleWorkLikeQueryPort.findSingleWorkIds(requestUserId, ids);

        return LikedSingleWorkIds.from(likedSet);
    }
}
