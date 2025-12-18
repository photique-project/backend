package com.benchpress200.photique.exhibition.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExhibitionException extends RuntimeException {
    private String originMessage;
    private final HttpStatus httpStatus;

    public ExhibitionException(
            String message,
            HttpStatus httpStatus
    ) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ExhibitionException(
            String message,
            String originMessage,
            HttpStatus httpStatus
    ) {
        super(message);
        this.originMessage = originMessage;
        this.httpStatus = httpStatus;
    }

}
