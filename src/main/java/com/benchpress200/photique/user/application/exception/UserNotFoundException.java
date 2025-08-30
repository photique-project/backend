package com.benchpress200.photique.user.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User with ID [" + userId + "] not found");
    }
}
