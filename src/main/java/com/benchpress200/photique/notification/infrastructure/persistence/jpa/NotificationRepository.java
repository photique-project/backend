package com.benchpress200.photique.notification.infrastructure.persistence.jpa;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndDeletedAtIsNull(Long id);

    List<Notification> findByReceiver(User receiver);

    boolean existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(Long receiverId);

    Page<Notification> findByReceiverIdAndDeletedAtIsNull(Long receiverId, Pageable pageable);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.isRead = true
            WHERE n.receiver.id = :receiverId
              AND n.isRead = false
              AND n.deletedAt IS NULL
            """)
    void markAllAsReadByReceiverIdAndDeletedAtIsNull(@Param("receiverId") Long receiverId);
}
