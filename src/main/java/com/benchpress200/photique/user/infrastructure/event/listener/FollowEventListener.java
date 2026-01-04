package com.benchpress200.photique.user.infrastructure.event.listener;

import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.event.FollowEvent;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FollowEventListener {
    private final UserQueryPort userQueryPort;
    private final NotificationCommandPort notificationCommandPort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowEventIfCommit(FollowEvent event) {
        // FIXME: 메시지 큐 도입하면 비동기 알림 생성 처리 고려
        Long followerId = event.getFollowerId();
        Long followeeId = event.getFolloweeId();

        User receiver = userQueryPort.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException(followeeId));

        Notification notification = Notification.of(
                receiver,
                NotificationType.FOLLOW,
                followerId
        );

        notificationCommandPort.save(notification);
    }
}
