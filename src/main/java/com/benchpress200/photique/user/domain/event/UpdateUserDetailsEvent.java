package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class UpdateUserDetailsEvent {
    private Long userId;

    private UpdateUserDetailsEvent(Long userId) {
        this.userId = userId;
    }

    public static UpdateUserDetailsEvent of(Long userId) {
        return new UpdateUserDetailsEvent(userId);
    }
}
