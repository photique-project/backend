package com.benchpress200.photique.common.exception;

import lombok.Getter;

@Getter
public class ImageUploaderException extends RuntimeException {
    private String originMessage;

    public ImageUploaderException(
            final String message,
            final String originMessage
    ) {
        super(message);
        this.originMessage = originMessage;
    }

    public ImageUploaderException(
            final String message
    ) {
        super(message);
    }
}
