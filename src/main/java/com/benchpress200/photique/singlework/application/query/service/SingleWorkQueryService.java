package com.benchpress200.photique.singlework.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.query.model.SearchSingleWorksQuery;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.support.LikedSingleWorkIds;
import com.benchpress200.photique.singlework.application.query.support.SearchedSingleWorks;
import com.benchpress200.photique.singlework.application.query.support.SingleWorkIds;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkSearchRepository;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkTagRepository;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkQueryService {
    private final AuthenticationUserProviderPort authenticationUserProvider;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final SingleWorkLikeRepository singleWorkLikeRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;
    private final FollowRepository followRepository;

    public SingleWorkDetailsResult getSingleWorkDetails(Long singleWorkId) {
        // 작품 조회 - 삭제된 데이터는 조회 X
        SingleWork singleWork = singleWorkRepository.findActiveWithWriter(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 태그 조회
        List<SingleWorkTag> singleWorkTags = singleWorkTagRepository.findWithTag(singleWork);
        List<Tag> tags = singleWorkTags.stream()
                .map(SingleWorkTag::getTag)
                .toList();

        // 좋아요 수 집계
        Long likeCount = singleWorkLikeRepository.countBySingleWork(singleWork);

        // 작품 좋아요 유무, 작가 팔로우 유무 확인
        boolean isLiked = false;
        boolean isFollowing = false;

        if (authenticationUserProvider.isAuthenticated()) {
            Long requestUserId = authenticationUserProvider.getCurrentUserId();
            Long writerId = singleWork.getWriter().getId();

            isLiked = singleWorkLikeRepository.existsByUserIdAndSingleWorkId(requestUserId, singleWorkId);
            isFollowing = followRepository.existsByFollowerIdAndFolloweeId(requestUserId, writerId);
        }

        return SingleWorkDetailsResult.of(
                singleWork,
                tags,
                likeCount,
                isLiked,
                isFollowing
        );
    }

    public SingleWorkSearchResult searchSingleWork(SearchSingleWorksQuery searchSingleWorksQuery) {
        Target target = searchSingleWorksQuery.getTarget();
        String keyword = searchSingleWorksQuery.getKeyword();
        List<Category> categories = searchSingleWorksQuery.getCategories();
        Pageable pageable = searchSingleWorksQuery.getPageable();

        Page<SingleWorkSearch> singleWorkSearchPage = singleWorkSearchRepository.search(
                target,
                keyword,
                categories,
                pageable
        );

        // 요청 유저가 좋아요한 작품 아이디 조회
        LikedSingleWorkIds likedSingleWorkIds = findLikedSingleWorkIds(singleWorkSearchPage);

        // 각 작품마다 유저의 좋아요 여부 확인
        SearchedSingleWorks searchedSingleWorks = SearchedSingleWorks.of(singleWorkSearchPage, likedSingleWorkIds);

        return SingleWorkSearchResult.of(searchedSingleWorks, singleWorkSearchPage);
    }

    private LikedSingleWorkIds findLikedSingleWorkIds(Page<SingleWorkSearch> singleWorkSearchPage) {
        // 인증된 유저가 아니라면
        if (!authenticationUserProvider.isAuthenticated()) {
            return LikedSingleWorkIds.empty();
        }

        Long requestUserId = authenticationUserProvider.getCurrentUserId();

        // 검색한 단일작품 페이지에서 단일작품 id를 일급 컬렉션으로 변환
        SingleWorkIds singleWorkIds = SingleWorkIds.from(singleWorkSearchPage);

        // 검색 결과 단일작품 중에서 요청 유저가 좋아요한 작품 아이디 셋 조회
        Set<Long> likedSet = singleWorkLikeRepository.findSingleWorkIds(requestUserId, singleWorkIds.values());

        return LikedSingleWorkIds.from(likedSet);
    }
}
