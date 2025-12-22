package com.benchpress200.photique.user.presentation.exception;

public class InvalidProfileImageException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid profile image";

    public InvalidProfileImageException() {
        super(DEFAULT_MESSAGE);
    }
}
