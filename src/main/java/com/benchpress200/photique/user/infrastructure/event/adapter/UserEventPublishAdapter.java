package com.benchpress200.photique.user.infrastructure.event.adapter;

import com.benchpress200.photique.user.application.command.port.out.event.UserEventPublishPort;
import com.benchpress200.photique.user.domain.event.ResisterEvent;
import com.benchpress200.photique.user.domain.event.UserDetailsUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublishAdapter implements UserEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishResisterEvent(ResisterEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishUserDetailsUpdateEvent(UserDetailsUpdateEvent event) {
        publisher.publishEvent(event);
    }
}
