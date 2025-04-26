package com.benchpress200.photique.user.application;

import com.benchpress200.photique.notification.domain.NotificationDomainService;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.Type;
import com.benchpress200.photique.user.domain.FollowDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.dto.FollowRequest;
import com.benchpress200.photique.user.domain.dto.FollowerResponse;
import com.benchpress200.photique.user.domain.dto.FollowingResponse;
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
    private final NotificationDomainService notificationDomainService;

    @Override
    @Transactional
    public void followUser(final FollowRequest followRequest) {
        // 팔로워 유저 조회
        Long followerId = followRequest.getFollowerId();
        User follower = userDomainService.findUser(followerId);

        // 팔로잉 유저 조회
        Long followingId = followRequest.getFollowingId();
        User following = userDomainService.findUser(followingId);

        // 팔로우 저장
        Follow follow = followRequest.toEntity(follower, following);
        followDomainService.createFollow(follow);

        // 알림 생성
        Notification notification = Notification.builder()
                .user(following)
                .type(Type.FOLLOW)
                .targetId(followerId)
                .build();

        // 알림 데이터 비동기 생성
        notificationDomainService.createNotification(notification);

        // 비동기처리
        notificationDomainService.pushNewNotification(followingId);
    }

    @Override
    @Transactional
    public void unfollowUser(final UnfollowRequest unfollowRequest) {
        // 팔로워 유저 조회
        Long followerId = unfollowRequest.getFollowerId();
        User follower = userDomainService.findUser(followerId);

        // 팔로잉 유저 조회
        Long followingId = unfollowRequest.getFollowingId();
        User following = userDomainService.findUser(followingId);

        // 본인이 팔로우하고있는 팔로잉 삭제
        followDomainService.deleteFollow(follower, following);
    }

    @Override
    @Transactional
    public Page<FollowerResponse> getFollowers(
            final Long userId,
            final Pageable pageable
    ) {
        // 유저조회
        User user = userDomainService.findUser(userId);

        // 본인을 팔로잉하고 있는 페이지 조회
        Page<Follow> followerPage = followDomainService.getFollowers(user, pageable);

        List<FollowerResponse> followerResponseList = followerPage.stream()
                .map(follower -> {
                    // 본인을 팔로워하는 사람들이고 본인도 팔로잉 상태인지 확인
                    boolean isFollowing = followDomainService.isFollowing(userId, follower.getFollower().getId());

                    return FollowerResponse.of(follower, isFollowing);
                })
                .toList();

        return new PageImpl<>(followerResponseList, pageable, followerPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<FollowingResponse> getFollowings(
            final Long userId,
            final Pageable pageable
    ) {
        // 유저조회
        User user = userDomainService.findUser(userId);

        // 본인이 팔로잉하고 있는 페이지 조회
        Page<Follow> followingPage = followDomainService.getFollowings(user, pageable);
        List<FollowingResponse> followingResponseList = followingPage.stream().map(FollowingResponse::from)
                .toList();

        return new PageImpl<>(followingResponseList, pageable, followingPage.getTotalElements());
    }
}
