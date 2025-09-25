package com.benchpress200.photique.auth.domain.exception;

public class MailAuthenticationCodeExpirationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Verification code has expired";

    public MailAuthenticationCodeExpirationException() {
        super(DEFAULT_MESSAGE);
    }

    public MailAuthenticationCodeExpirationException(String message) {
        super(message);
    }
}
