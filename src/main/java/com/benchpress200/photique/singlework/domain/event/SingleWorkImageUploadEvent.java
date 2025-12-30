package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkImageUploadEvent {
    private String imageUrl;

    private SingleWorkImageUploadEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static SingleWorkImageUploadEvent of(String imageUrl) {
        return new SingleWorkImageUploadEvent(imageUrl);
    }
}
