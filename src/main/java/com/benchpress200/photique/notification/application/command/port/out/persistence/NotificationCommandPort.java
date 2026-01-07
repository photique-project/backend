package com.benchpress200.photique.notification.application.command.port.out.persistence;

import com.benchpress200.photique.notification.domain.entity.Notification;
import java.util.List;

public interface NotificationCommandPort {
    Notification save(Notification notification);

    List<Notification> saveAll(List<Notification> notifications);

    void markAllAsReadByReceiverId(Long receiverId);
}
