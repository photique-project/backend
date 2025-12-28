package com.benchpress200.photique.auth.domain.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String email) {
        super("User with email [" + email + "] is not found");
    }
}
