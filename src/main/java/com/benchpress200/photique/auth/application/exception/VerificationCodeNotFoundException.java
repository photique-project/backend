package com.benchpress200.photique.auth.application.exception;

public class VerificationCodeNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "The verification code has expired";

    public VerificationCodeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
