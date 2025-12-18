package com.benchpress200.photique.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private String originMessage;
    private final HttpStatus httpStatus;

    public UserException(
            String message,
            HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public UserException(
            String message,
            String originMessage,
            HttpStatus httpStatus
    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }
}
