package com.benchpress200.photique.user.domain.exception;

public class AlreadyUnfollowException extends RuntimeException {
    public AlreadyUnfollowException() {
        super();
    }

    public AlreadyUnfollowException(String message) {
        super(message);
    }
}
