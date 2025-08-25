package com.benchpress200.photique.image.domain.exception;

public class ImageUploaderFileWriteException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "IOException: Image Conversion Failed";

    public ImageUploaderFileWriteException() {
        super(DEFAULT_MESSAGE);
    }
}
