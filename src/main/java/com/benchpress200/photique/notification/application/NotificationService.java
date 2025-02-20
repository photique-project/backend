package com.benchpress200.photique.notification.application;

import com.benchpress200.photique.notification.domain.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(Long userId);

    Page<NotificationResponse> getNotifications(Long userId, Pageable pageable);

    void markAsRead(Long userId, Long notificationId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long userId, Long notificationId);
}
