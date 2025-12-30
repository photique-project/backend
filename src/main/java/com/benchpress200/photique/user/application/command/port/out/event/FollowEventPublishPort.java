package com.benchpress200.photique.user.application.command.port.out.event;

import com.benchpress200.photique.user.domain.event.FollowEvent;

public interface FollowEventPublishPort {
    void publishFollowEvent(FollowEvent followEvent);
}
