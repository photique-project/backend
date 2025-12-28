package com.benchpress200.photique.auth.infrastructure.exception;


public class LoginRequestObjectReadException extends RuntimeException {
    public LoginRequestObjectReadException(String message) {
        super(message);
    }
}
