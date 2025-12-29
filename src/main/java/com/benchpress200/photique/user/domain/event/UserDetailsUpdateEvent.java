package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class UserDetailsUpdateEvent {
    private String oldProfileImageUrl;
    private String newProfileImageUrl;
    private Long userId;

    private UserDetailsUpdateEvent() {
    }

    public static UserDetailsUpdateEvent empty() {
        return new UserDetailsUpdateEvent();
    }

    public void addOldProfileImageUrl(String oldProfileImageUrl) {
        this.oldProfileImageUrl = oldProfileImageUrl;
    }

    public void addNewProfileImageUrl(String newProfileImageUrl) {
        this.newProfileImageUrl = newProfileImageUrl;
    }

    public void addUserId(Long userId) {
        this.userId = userId;
    }

    public boolean existsOldProfileImageUrl() {
        return oldProfileImageUrl != null;
    }

    public boolean existsNewProfileImageUrl() {
        return newProfileImageUrl != null;
    }
}
