package com.benchpress200.photique.common.response;

import org.springframework.http.HttpStatus;

public class ResponseHandler {
    public static <T> ApiSuccessResponse<T> handleSuccessResponse(final T data, final HttpStatus status) {
        return new ApiSuccessResponse<>(data, status);
    }

    public static <T> ApiSuccessResponse<T> handleSuccessResponse(final HttpStatus status) {
        return new ApiSuccessResponse<>(status);
    }

    public static ApiFailureResponse handleFailureResponse(final String message, final HttpStatus status) {
        return new ApiFailureResponse(message, status);
    }

    public static ApiFailureResponse handleFailureResponse(final HttpStatus status) {
        return new ApiFailureResponse(status);
    }
}
