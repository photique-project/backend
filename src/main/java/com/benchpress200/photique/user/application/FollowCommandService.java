package com.benchpress200.photique.user.application;

import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.notification.domain.repository.NotificationRepository;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.repository.FollowRepository;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.presentation.exception.InvalidFollowRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowCommandService {
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    @Transactional
    public void follow(final Long followeeId) {
        // 팔로워 유저 조회
        Long followerId = authenticationUserProviderPort.getCurrentUserId();

        if (followerId.equals(followeeId)) {
            throw new InvalidFollowRequestException();
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로잉(팔로우 대상) 유저 조회
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException(followerId));

        // 팔로우 엔티티 저장
        Follow follow = Follow.of(follower, followee);
        followRepository.save(follow);

        // 팔로잉 유저(팔로우 요청받은 유저) 알림 데이터 저장
        Notification notification = Notification.of(
                followee,
                NotificationType.FOLLOW,
                followerId
        );

        notificationRepository.save(notification);
    }
}
