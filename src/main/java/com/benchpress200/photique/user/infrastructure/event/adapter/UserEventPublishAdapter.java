package com.benchpress200.photique.user.infrastructure.event.adapter;

import com.benchpress200.photique.user.domain.event.UpdateUserDetailsEvent;
import com.benchpress200.photique.user.domain.port.event.UserEventPublishPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublishAdapter implements UserEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishUpdateUserDetailsEvent(UpdateUserDetailsEvent updateUserDetailsEvent) {
        publisher.publishEvent(updateUserDetailsEvent);
    }
}
