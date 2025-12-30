package com.benchpress200.photique.user.infrastructure.event.adapter;

import com.benchpress200.photique.user.application.command.port.out.event.FollowEventPublishPort;
import com.benchpress200.photique.user.domain.event.FollowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowEventPublishAdapter implements FollowEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishFollowEvent(FollowEvent event) {
        publisher.publishEvent(event);
    }
}
