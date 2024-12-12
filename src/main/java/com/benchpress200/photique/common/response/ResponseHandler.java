package com.benchpress200.photique.common.response;

import org.springframework.http.HttpStatus;

public class ResponseHandler {
    public static <T> ApiSuccessResponse<T> handleSuccessResponse(T data, HttpStatus status) {
        return new ApiSuccessResponse<>(data, status);
    }

    public static ApiSuccessResponse handleSuccessResponse(HttpStatus status) {
        return new ApiSuccessResponse<>(status);
    }

    public static ApiFailureResponse handleFailureResponse(String message, HttpStatus status) {
        return new ApiFailureResponse(message, status);
    }

    public static ApiFailureResponse handleFailureResponse(HttpStatus status) {
        return new ApiFailureResponse(status);
    }
}
