package com.benchpress200.photique.user.domain.port.event;

import com.benchpress200.photique.user.domain.event.UpdateUserDetailsEvent;

public interface UserEventPublishPort {
    void publishUpdateUserDetailsEvent(UpdateUserDetailsEvent updateUserDetailsEvent);
}
