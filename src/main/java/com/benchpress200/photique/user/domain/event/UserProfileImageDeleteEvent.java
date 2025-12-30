package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class UserProfileImageDeleteEvent {
    private String imageUrl;

    private UserProfileImageDeleteEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static UserProfileImageDeleteEvent of(String imageUrl) {
        return new UserProfileImageDeleteEvent(imageUrl);
    }
}
