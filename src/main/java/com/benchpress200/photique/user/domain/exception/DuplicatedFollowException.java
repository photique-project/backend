package com.benchpress200.photique.user.domain.exception;

public class DuplicatedFollowException extends RuntimeException {
    public DuplicatedFollowException() {
        super();
    }

    public DuplicatedFollowException(String message) {
        super(message);
    }
}
