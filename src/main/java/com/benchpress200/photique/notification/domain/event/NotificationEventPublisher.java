package com.benchpress200.photique.notification.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishNewSingleWorkNotificationEventIfCommit(Long singleWorkId) {
        NewSingleWorkNotificationCommitEvent event = new NewSingleWorkNotificationCommitEvent(singleWorkId);
        publisher.publishEvent(event);
    }
}
