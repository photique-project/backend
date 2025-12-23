package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.singlework.application.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkLikeRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkTagRepository;
import com.benchpress200.photique.tag.domain.entity.Tag;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkQueryService {
    private final AuthenticationUserProviderPort authenticationUserProvider;
    private final SingleWorkRepository singleWorkRepository;
    private final SingleWorkTagRepository singleWorkTagRepository;
    private final SingleWorkLikeRepository singleWorkLikeRepository;
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
}
