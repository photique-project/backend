package com.benchpress200.photique.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {
    private final HttpStatus httpStatus;
    private String originMessage;

    public AuthException(
            String message,
            HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AuthException(
            final String message,
            final String originMessage,
            final HttpStatus httpStatus

    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }
}
