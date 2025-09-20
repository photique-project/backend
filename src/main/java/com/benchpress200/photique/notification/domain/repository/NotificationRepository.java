package com.benchpress200.photique.notification.domain.repository;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);

    List<Notification> findByReceiver(User receiver);

    Long countByReceiverAndIsReadFalse(User receiver);
}
