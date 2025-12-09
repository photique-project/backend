package com.benchpress200.photique.auth.application.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(final String email) {
        super("User with email [" + email + "] is not found");
    }
}
