package com.benchpress200.photique.notification.infrastructure.persistence.jpa;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverOrderByCreatedAtDesc(User receiver, Pageable pageable);

    List<Notification> findByReceiver(User receiver);

    boolean existsByReceiverIdAndIsReadFalse(Long receiverId);

    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.receiver.id = :receiverId
              AND n.isRead = false
            """)
    void markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);
}
