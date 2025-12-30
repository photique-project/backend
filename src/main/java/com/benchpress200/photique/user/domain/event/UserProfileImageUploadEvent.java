package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class UserProfileImageUploadEvent {
    private String imageUrl;

    private UserProfileImageUploadEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static UserProfileImageUploadEvent of(String imageUrl) {
        return new UserProfileImageUploadEvent(imageUrl);
    }
}
