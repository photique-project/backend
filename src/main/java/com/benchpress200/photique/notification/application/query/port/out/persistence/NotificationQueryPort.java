package com.benchpress200.photique.notification.application.query.port.out.persistence;

import com.benchpress200.photique.notification.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationQueryPort {
    Page<Notification> findByReceiverIdAndDeletedAtIsNull(Long receiverId, Pageable pageable);

    boolean existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(Long receiverId);

    Optional<Notification> findByIdAndDeletedAtIsNull(Long id);
}
