package com.benchpress200.photique.user.presentation.command.exception;

public class InvalidProfileImageException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid profile image";

    public InvalidProfileImageException() {
        super(DEFAULT_MESSAGE);
    }
}
