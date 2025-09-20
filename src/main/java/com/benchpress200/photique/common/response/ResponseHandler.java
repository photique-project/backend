package com.benchpress200.photique.common.response;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    public static <T> ApiSuccessResponse<T> handleSuccessResponse(final T data, final HttpStatus status) {
        return new ApiSuccessResponse<>(data, status);
    }

    public static <T> ApiSuccessResponse<T> handleSuccessResponse(final HttpStatus status) {
        return new ApiSuccessResponse<>(status);
    }

    public static <T> ResponseEntity<?> handleResponse(
            final HttpStatus status,
            final String message,
            final T data
    ) {
        return ResponseEntity
                .status(status)
                .body(new ResponseBody<>(
                        status.value(),
                        message,
                        data,
                        LocalDateTime.now()
                ));
    }

    public static ResponseEntity<?> handleResponse(
            final HttpStatus status,
            final String message
    ) {
        return ResponseEntity
                .status(status)
                .body(new ResponseBody<>(
                        status.value(),
                        message,
                        null,
                        LocalDateTime.now()
                ));
    }

    public static ResponseEntity<?> handleResponse(
            final HttpStatus status
    ) {
        return ResponseEntity
                .status(status).build();
    }
}
