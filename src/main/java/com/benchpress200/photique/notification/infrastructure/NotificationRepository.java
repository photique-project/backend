package com.benchpress200.photique.notification.infrastructure;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);

    List<Notification> findByUser(User user);
}
