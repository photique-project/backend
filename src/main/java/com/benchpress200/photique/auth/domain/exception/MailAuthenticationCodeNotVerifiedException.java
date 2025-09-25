package com.benchpress200.photique.auth.domain.exception;

public class MailAuthenticationCodeNotVerifiedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Verification has not been completed yet";

    public MailAuthenticationCodeNotVerifiedException() {
        super(DEFAULT_MESSAGE);
    }

    public MailAuthenticationCodeNotVerifiedException(String message) {
        super(message);
    }
}
