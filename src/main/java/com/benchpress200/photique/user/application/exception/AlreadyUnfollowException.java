package com.benchpress200.photique.user.application.exception;

public class AlreadyUnfollowException extends RuntimeException {
    public AlreadyUnfollowException() {
        super();
    }

    public AlreadyUnfollowException(String message) {
        super(message);
    }
}
