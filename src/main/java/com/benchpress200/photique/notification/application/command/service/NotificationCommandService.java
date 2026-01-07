package com.benchpress200.photique.notification.application.command.service;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.application.command.port.in.DeleteNotificationUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAllAsReadUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAsReadUseCase;
import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.exception.NotificationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService implements
        MarkAsReadUseCase,
        MarkAllAsReadUseCase,
        DeleteNotificationUseCase {
    private final AuthenticationUserProviderPort authenticationUserProviderPort;

    private final NotificationCommandPort notificationCommandPort;
    private final NotificationQueryPort notificationQueryPort;

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationQueryPort.findByIdAndDeletedAtIsNull(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notification.read();
    }

    @Override
    public void markAllAsRead() {
        Long userId = authenticationUserProviderPort.getCurrentUserId();
        notificationCommandPort.markAllAsReadByReceiverIdAndDeletedAtIsNull(userId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationQueryPort.findByIdAndDeletedAtIsNull(notificationId)
                .ifPresent(Notification::delete);
    }
}
