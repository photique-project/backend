package com.benchpress200.photique.auth.domain.exception;

public class UnauthenticatedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Authentication failed";

    public UnauthenticatedException() {
        super(DEFAULT_MESSAGE);
    }
}
