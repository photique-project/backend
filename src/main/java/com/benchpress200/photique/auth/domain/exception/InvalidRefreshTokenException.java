package com.benchpress200.photique.auth.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Authentication failed";

    public InvalidRefreshTokenException() {
        super(DEFAULT_MESSAGE);
    }
}
