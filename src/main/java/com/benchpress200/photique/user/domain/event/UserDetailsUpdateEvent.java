package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class UserDetailsUpdateEvent {
    private Long userId;

    private UserDetailsUpdateEvent(Long userId) {
        this.userId = userId;
    }

    public static UserDetailsUpdateEvent of(Long userId) {
        return new UserDetailsUpdateEvent(userId);
    }
}
