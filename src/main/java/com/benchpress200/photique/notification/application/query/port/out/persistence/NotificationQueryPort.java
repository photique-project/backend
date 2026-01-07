package com.benchpress200.photique.notification.application.query.port.out.persistence;

import com.benchpress200.photique.notification.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationQueryPort {
    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);

    boolean existsByReceiverIdAndIsReadFalse(Long receiverId);
}
