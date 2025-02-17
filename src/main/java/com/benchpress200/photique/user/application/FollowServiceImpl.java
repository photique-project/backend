package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.FollowRequest;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserDomainService userDomainService;
    private final FollowDomainService followDomainService;

    @Override
    public void followUser(final FollowRequest followRequest) {
        // 팔로워 유저 조회
        Long followerId = followRequest.getFollowerId();
        User follower = userDomainService.findUser(followerId);

        // 팔로잉 유저  조회
        Long followingId = followRequest.getFollowerId();
        User following = userDomainService.findUser(followingId);

        // 팔로우 저장
        Follow follow = followRequest.toEntity(follower, following);
        followDomainService.createFollow(follow);
    }
}
