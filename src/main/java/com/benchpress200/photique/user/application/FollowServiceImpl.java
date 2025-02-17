package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.FollowRequest;
import com.benchpress200.photique.user.domain.dto.FollowerResponse;
import com.benchpress200.photique.user.domain.dto.UnfollowRequest;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserDomainService userDomainService;
    private final FollowDomainService followDomainService;

    @Override
    @Transactional
    public void followUser(final FollowRequest followRequest) {
        // 팔로워 유저 조회
        Long followerId = followRequest.getFollowerId();
        User follower = userDomainService.findUser(followerId);

        // 팔로잉 유저 조회
        Long followingId = followRequest.getFollowerId();
        User following = userDomainService.findUser(followingId);

        // 팔로우 저장
        Follow follow = followRequest.toEntity(follower, following);
        followDomainService.createFollow(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(UnfollowRequest unfollowRequest) {
        // 팔로워 유저 조회
        Long followerId = unfollowRequest.getFollowerId();
        User follower = userDomainService.findUser(followerId);

        // 팔로잉 유저 조회
        Long followingId = unfollowRequest.getFollowerId();
        User following = userDomainService.findUser(followingId);

        // 본인이 팔로우하고있는 팔로잉 삭제
        followDomainService.deleteFollow(follower, following);
    }

    @Override
    public Page<FollowerResponse> getFollowers(Long userId, Pageable pageable) {
        // 유저조회
        User user = userDomainService.findUser(userId);

        // 본인을 팔로잉하고 있는 페이지 조회
        Page<Follow> followerPage = followDomainService.getFollowers(user, pageable);
        List<FollowerResponse> followerResponseList = followerPage.stream().map(FollowerResponse::from)
                .toList();

        return new PageImpl<>(followerResponseList, pageable, followerPage.getTotalElements());
    }
}
