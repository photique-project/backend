package com.benchpress200.photique.user.application.command.service;

import com.benchpress200.photique.auth.domain.port.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.notification.domain.repository.NotificationRepository;
import com.benchpress200.photique.user.application.command.port.in.FollowUseCase;
import com.benchpress200.photique.user.application.command.port.in.UnfollowUseCase;
import com.benchpress200.photique.user.application.command.port.out.persistence.FollowCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.exception.AlreadyUnfollowException;
import com.benchpress200.photique.user.domain.exception.DuplicatedFollowException;
import com.benchpress200.photique.user.domain.exception.InvalidFollowRequestException;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowCommandService implements
        FollowUseCase,
        UnfollowUseCase {
    private final FollowCommandPort followCommandPort;
    private final FollowQueryPort followQueryPort;
    private final UserQueryPort userQueryPort;
    private final NotificationRepository notificationRepository;
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    @Transactional
    public void follow(Long followeeId) {
        // 팔로워 유저 조회
        Long followerId = authenticationUserProviderPort.getCurrentUserId();

        // 본인을 팔로우 한다면
        if (followerId.equals(followeeId)) {
            throw new InvalidFollowRequestException();
        }

        // 이미 팔로우 관계 데이터가 있다면 바로 성공 응답줘서 멱등 처리
        followQueryPort.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .ifPresent(follow -> {
                    throw new DuplicatedFollowException();
                });

        // 팔로워 유저 조회
        User follower = userQueryPort.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로잉(팔로우 대상) 유저 조회
        User followee = userQueryPort.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로우 엔티티 저장
        Follow follow = Follow.of(follower, followee);
        followCommandPort.save(follow);

        // 팔로잉 유저(팔로우 요청받은 유저) 알림 데이터 저장
        Notification notification = Notification.of(
                followee,
                NotificationType.FOLLOW,
                followerId
        );

        notificationRepository.save(notification);
    }


    @Transactional
    public void unfollow(Long followeeId) {
        // 팔로워 유저 조회
        Long followerId = authenticationUserProviderPort.getCurrentUserId();

        // 본인을 언팔로우 한다면
        if (followerId.equals(followeeId)) {
            throw new InvalidFollowRequestException();
        }

        // 이미 팔로우 관계 데이터가 없다면 바로 성공 응답줘서 멱등 처리
        Follow follow = followQueryPort.findByFollowerIdAndFolloweeId(followerId, followeeId)
                .orElseThrow(AlreadyUnfollowException::new);

        // 팔로워 유저 조회
        userQueryPort.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로잉(팔로우 대상) 유저 조회
        userQueryPort.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로우 데이터 삭제
        followCommandPort.delete(follow);
    }
}
