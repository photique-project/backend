package com.benchpress200.photique.notification.application;

import com.benchpress200.photique.notification.domain.NotificationDomainService;
import com.benchpress200.photique.notification.domain.dto.CountUnreadResponse;
import com.benchpress200.photique.notification.domain.dto.NotificationResponse;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.UserDomainService;
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
public class NotificationServiceImpl implements NotificationService {

    private final UserDomainService userDomainService;
    private final NotificationDomainService notificationDomainService;

    @Override
    public Page<NotificationResponse> getNotifications(
            final Long userId,
            final Pageable pageable
    ) {
        // 유저 조회
        User user = userDomainService.findUser(userId);

        // 알림 리스트 조회
        Page<Notification> notificationPage = notificationDomainService.findNotifications(user, pageable);

        List<NotificationResponse> notificationResponsesPage = notificationPage.stream()
                .map(NotificationResponse::from)
                .toList();

        return new PageImpl<>(notificationResponsesPage, pageable, notificationPage.getTotalElements());
    }

    @Override
    @Transactional
    public void markAsRead(
            final Long userId,
            final Long notificationId
    ) {
        // 유저 조회
        userDomainService.findUser(userId);

        // 노티 조회
        Notification notification = notificationDomainService.findNotification(notificationId);

        // 읽음처리
        notificationDomainService.readNotification(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        // 유저 조회
        User user = userDomainService.findUser(userId);

        // 노티 조회
        List<Notification> notifications = notificationDomainService.findNotifications(user);

        // 순회하면서 읽음처리
        notifications.forEach(notificationDomainService::readNotification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        // 유저 조회
        User user = userDomainService.findUser(userId);

        // 노티 조회
        Notification notification = notificationDomainService.findNotification(notificationId);

        // 노티 삭제
        notificationDomainService.deleteNotification(notification);
    }

    @Override
    @Transactional
    public CountUnreadResponse countUnread(final Long userId) {
        // 유저 조회
        User user = userDomainService.findUser(userId);

        // 카운트횟수 반환해서 DTO만들기
        long count = notificationDomainService.countUnread(user);

        return CountUnreadResponse.from(count);
    }
}
