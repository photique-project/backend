package com.benchpress200.photique.common.response;

import com.benchpress200.photique.common.response.ApiSuccessResponse.SuccessData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiSuccessResponse<T> extends ResponseEntity<SuccessData<T>> {
    public ApiSuccessResponse(final T data, final HttpStatus status) {
        super(new SuccessData<>(data), status);
    }

    public ApiSuccessResponse(final HttpStatus status) {
        super(status);
    }

    public record SuccessData<T>(
            @JsonProperty("data") T data
    ) {
    }
}
