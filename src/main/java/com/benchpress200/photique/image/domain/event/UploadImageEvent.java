package com.benchpress200.photique.image.domain.event;

import lombok.Getter;

@Getter
public class UploadImageEvent {
    private String imageUrl;

    private UploadImageEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static UploadImageEvent of(String imageUrl) {
        return new UploadImageEvent(imageUrl);
    }
}
