package com.benchpress200.photique.image.domain.event;

import lombok.Getter;

@Getter
public class DeleteImageEvent {
    private String imageUrl;

    private DeleteImageEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static DeleteImageEvent of(String imageUrl) {
        return new DeleteImageEvent(imageUrl);
    }
}
