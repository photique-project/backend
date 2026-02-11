package com.benchpress200.photique.user.infrastructure.event.adapter;

import com.benchpress200.photique.user.application.command.port.out.event.UserEventPublishPort;
import com.benchpress200.photique.user.domain.event.UserProfileImageDeleteEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublishAdapter implements UserEventPublishPort {
    private final ApplicationEventPublisher publisher;


    @Override
    public void publishUserProfileImageUploadEvent(UserProfileImageUploadEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishUserProfileImageDeleteEvent(UserProfileImageDeleteEvent event) {
        publisher.publishEvent(event);
    }
}
