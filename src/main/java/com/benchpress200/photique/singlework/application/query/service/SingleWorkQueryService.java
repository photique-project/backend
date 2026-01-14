package com.benchpress200.photique.singlework.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.query.model.MySingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMySingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.out.event.SingleWorkViewCountPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
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

    private final SingleWorkViewCountPort singleWorkViewCountPort;
    private final SingleWorkCommandPort singleWorkCommandPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkTagQueryPort singleWorkTagQueryPort;
    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    private final FollowQueryPort followQueryPort;

    @Override
    public SingleWorkDetailsResult getSingleWorkDetails(Long singleWorkId) {
        // 작품 조회
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
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
        
        // 조회수 증가
        try {
            singleWorkViewCountPort.incrementViewCount(singleWorkId);
        } catch (RuntimeException e) { // fallback 처리
            singleWorkCommandPort.incrementViewCount(singleWorkId);
        }

        return SingleWorkDetailsResult.of(
                singleWork,
                tags,
                isLiked,
                isFollowing
        );
    }

    @Override
    public SingleWorkSearchResult searchSingleWork(SingleWorkSearchQuery query) {
        Target target = query.getTarget();
        String keyword = query.getKeyword();
        List<Category> categories = query.getCategories();
        Pageable pageable = query.getPageable();

        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkQueryPort.searchSingleWork(
                target,
                keyword,
                categories,
                pageable
        );

        List<Long> singleWorkSearchIds = singleWorkSearchPage.stream()
                .map(SingleWorkSearch::getId)
                .toList();

        // 요청 유저가 좋아요한 작품 아이디 조회
        Ids likedSingleWorkIds = findLikedSingleWorkIds(singleWorkSearchIds);

        // 결과 반환
        return SingleWorkSearchResult.of(singleWorkSearchPage, likedSingleWorkIds);
    }

    @Override
    public MySingleWorkSearchResult searchMySingleWork(MySingleWorkSearchQuery query) {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        String keyword = query.getKeyword();
        Pageable pageable = query.getPageable();

        // 본인 단일작품 페이지 조회
        Page<SingleWork> singleWorkPage = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                userId,
                keyword,
                pageable
        );

        // 요청 유저가 좋아요한 작품 아이디 조회
        List<Long> singleWorkIds = singleWorkPage.stream()
                .map(SingleWork::getId)
                .toList();

        Ids likedSingleWorkIds = findLikedSingleWorkIds(singleWorkIds);

        // 결과 반환
        return MySingleWorkSearchResult.of(singleWorkPage, likedSingleWorkIds);
    }

    private Ids findLikedSingleWorkIds(List<Long> ids) {
        // 인증된 유저가 아니라면
        if (!authenticationUserProviderPort.isAuthenticated()) {
            return Ids.empty();
        }

        Long requestUserId = authenticationUserProviderPort.getCurrentUserId();

        // 검색 결과 단일작품 중에서 요청 유저가 좋아요한 작품 아이디 셋 조회
        Set<Long> likedSet = singleWorkLikeQueryPort.findSingleWorkIds(requestUserId, ids);

        return Ids.from(likedSet);
    }
}
