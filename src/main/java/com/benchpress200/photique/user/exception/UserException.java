package com.benchpress200.photique.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {
    private String originMessage;
    private HttpStatus httpStatus;

    public UserException(
          final String message,
          final HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public UserException(
            final String message,
            final String originMessage,
            final HttpStatus httpStatus
    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }
}
