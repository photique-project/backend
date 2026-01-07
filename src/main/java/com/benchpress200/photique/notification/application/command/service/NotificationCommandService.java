package com.benchpress200.photique.notification.application.command.service;

import com.benchpress200.photique.notification.application.command.port.in.MarkAsReadUseCase;
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
        MarkAsReadUseCase {
    private final NotificationQueryPort notificationQueryPort;

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationQueryPort.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notification.read();
    }
}
