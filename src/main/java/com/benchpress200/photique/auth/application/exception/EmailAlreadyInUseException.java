package com.benchpress200.photique.auth.application.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("Email [" + email + "] is already in use");
    }
}
