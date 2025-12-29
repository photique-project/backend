package com.benchpress200.photique.image.infrastructure.exception;

import lombok.Getter;

@Getter
public class ImageDeleteException extends RuntimeException {
    private String imageUrl;

    public ImageDeleteException(
            final String message,
            final String imageUrl
    ) {
        super(message);
        this.imageUrl = imageUrl;
    }
}
