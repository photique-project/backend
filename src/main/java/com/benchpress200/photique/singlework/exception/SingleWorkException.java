package com.benchpress200.photique.singlework.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SingleWorkException extends RuntimeException {
    private String originMessage;
    private final HttpStatus httpStatus;

    public SingleWorkException(
            String message,
            HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public SingleWorkException(
            String message,
            String originMessage,
            HttpStatus httpStatus
    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }
}
