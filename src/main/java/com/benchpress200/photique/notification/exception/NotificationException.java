package com.benchpress200.photique.notification.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotificationException extends RuntimeException {
    private String originMessage;
    private final HttpStatus httpStatus;

    public NotificationException(
            final String message,
            final HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public NotificationException(
            final String message,
            final String originMessage,
            final HttpStatus httpStatus
    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }
}
