package com.benchpress200.photique.user.application.exception;

public class DuplicateFollowRequestException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Already following";

    public DuplicateFollowRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public DuplicateFollowRequestException(String message) {
        super(message);
    }
}
