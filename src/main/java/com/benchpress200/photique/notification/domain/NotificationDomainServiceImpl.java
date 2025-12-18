package com.benchpress200.photique.notification.domain;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.repository.NotificationRepository;
import com.benchpress200.photique.notification.exception.NotificationException;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDomainServiceImpl implements NotificationDomainService {
    private final NotificationRepository notificationRepository;

    @Override
    public Page<Notification> findNotifications(
            User user,
            Pageable pageable
    ) {
        Page<Notification> notificationPage = notificationRepository.findByReceiverOrderByCreatedAtDesc(user, pageable);

        if (notificationPage.getTotalElements() == 0) {
            throw new NotificationException("No notifications found.", HttpStatus.NOT_FOUND);
        }
        return notificationPage;
    }

    @Override
    public Notification findNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotificationException("Notification with id " + notificationId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void readNotification(Notification notification) {
        notification.read();
    }

    @Override
    public List<Notification> findNotifications(User user) {
        return notificationRepository.findByReceiver(user);
    }

    @Override
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }

    @Override
    public long countUnread(User user) {
        return notificationRepository.countByReceiverAndIsReadFalse(user);
    }
}
