package com.benchpress200.photique.user.application.command.port.out.event;

import com.benchpress200.photique.user.domain.event.ResisterEvent;
import com.benchpress200.photique.user.domain.event.UserDetailsUpdateEvent;

public interface UserEventPublishPort {
    void publishResisterEvent(ResisterEvent event);

    void publishUserDetailsUpdateEvent(UserDetailsUpdateEvent event);
}
