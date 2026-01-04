package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionWorkImageUploadEvent {
    private String imageUrl;

    private ExhibitionWorkImageUploadEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ExhibitionWorkImageUploadEvent of(String imageUrl) {
        return new ExhibitionWorkImageUploadEvent(imageUrl);
    }
}
