package com.benchpress200.photique.image.infrastructure.exception;

import lombok.Getter;

@Getter
public class S3DeleteException extends RuntimeException {
    private String imageUrl;

    public S3DeleteException(
            final String message,
            final String imageUrl
    ) {
        super(message);
        this.imageUrl = imageUrl;
    }
}
