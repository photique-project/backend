package com.benchpress200.photique.user.application.command.port.out.event;

import com.benchpress200.photique.user.domain.event.UserDetailsUpdateEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageDeleteEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageUploadEvent;

public interface UserEventPublishPort {
    void publishUserProfileImageUploadEvent(UserProfileImageUploadEvent event);

    void publishUserProfileImageDeleteEvent(UserProfileImageDeleteEvent event);

    void publishUserDetailsUpdateEvent(UserDetailsUpdateEvent event);
}
