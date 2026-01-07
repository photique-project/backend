package com.benchpress200.photique.notification.application.query.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import com.benchpress200.photique.notification.application.query.port.in.GetNotificationPageUserCase;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import com.benchpress200.photique.notification.domain.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryService implements
        GetNotificationPageUserCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    private final NotificationQueryPort notificationQueryPort;

    @Override
    public NotificationPageResult getNotificationPage(NotificationPageQuery query) {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        Pageable pageable = query.getPageable();

        Page<Notification> notificationpage = notificationQueryPort.findByReceiverIdAndDeletedAtIsNull(userId,
                pageable);
        boolean unread = notificationQueryPort.existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(userId);

        return NotificationPageResult.of(notificationpage, unread);
    }
}
