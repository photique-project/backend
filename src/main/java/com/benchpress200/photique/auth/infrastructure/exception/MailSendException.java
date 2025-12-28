package com.benchpress200.photique.auth.infrastructure.exception;

public class MailSendException extends RuntimeException {
    public MailSendException(String message) {
        super(message);
    }
}
