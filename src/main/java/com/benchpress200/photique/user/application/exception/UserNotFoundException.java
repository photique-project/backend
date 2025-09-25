package com.benchpress200.photique.user.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final Long userId) {
        super("User with id [" + userId + "] not found");
    }

    public UserNotFoundException(final String email) {
        super("User with email [" + email + "] not found");
    }
}
