package com.benchpress200.photique.singlework.presentation.command.exception;

public class InvalidImageException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid image";

    public InvalidImageException() {
        super(DEFAULT_MESSAGE);
    }
}
