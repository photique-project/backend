package com.benchpress200.photique.notification.infrastructure.persistence.adapter;

import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.infrastructure.persistence.jpa.NotificationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements
        NotificationCommandPort,
        NotificationQueryPort {
    private final NotificationRepository notificationRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        return notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAllAsReadByReceiverIdAndDeletedAtIsNull(Long receiverId) {
        notificationRepository.markAllAsReadByReceiverIdAndDeletedAtIsNull(receiverId);
    }

    @Override
    public Page<Notification> findByReceiverIdAndDeletedAtIsNull(Long receiverId, Pageable pageable) {
        return notificationRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, pageable);
    }

    @Override
    public boolean existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(Long receiverId) {
        return notificationRepository.existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(receiverId);
    }

    @Override
    public Optional<Notification> findByIdAndDeletedAtIsNull(Long id) {
        return notificationRepository.findByIdAndDeletedAtIsNull(id);
    }
}
