package com.benchpress200.photique.user.domain.event;

import lombok.Getter;

@Getter
public class ResisterEvent {
    private String imageUrl;

    private ResisterEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ResisterEvent of(String imageUrl) {
        return new ResisterEvent(imageUrl);
    }
}
