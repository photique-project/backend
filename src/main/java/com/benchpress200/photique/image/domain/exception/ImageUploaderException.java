package com.benchpress200.photique.image.domain.exception;

import lombok.Getter;

@Getter
public class ImageUploaderException extends RuntimeException {
    private String originMessage; // 500 상태코드 고정이라서 상태코드 안받음

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
