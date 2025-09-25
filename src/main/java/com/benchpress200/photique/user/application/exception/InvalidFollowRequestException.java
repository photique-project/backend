package com.benchpress200.photique.user.application.exception;

public class InvalidFollowRequestException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You cannot follow yourself";

    public InvalidFollowRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidFollowRequestException(String message) {
        super(message);
    }
}
