package com.benchpress200.photique.notification.domain;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationDomainService {
    SseEmitter subscribe(Long userId);

    void createNotification(Notification notification);

    void pushNewNotification(Long userId);

    Page<Notification> findNotifications(User user, Pageable pageable);

    Notification findNotification(Long notificationId);

    void readNotification(Notification notification);

    List<Notification> findNotifications(User user);

    void deleteNotification(Notification notification);

    long countUnread(User user);
}
