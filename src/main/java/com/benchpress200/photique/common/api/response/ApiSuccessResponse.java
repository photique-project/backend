package com.benchpress200.photique.common.api.response;

import com.benchpress200.photique.common.api.response.ApiSuccessResponse.SuccessData;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiSuccessResponse<T> extends ResponseEntity<SuccessData<T>> {
    public ApiSuccessResponse(T data, HttpStatus status) {
        super(new SuccessData<>(data), status);
    }

    public ApiSuccessResponse(HttpStatus status) {
        super(status);
    }

    public record SuccessData<T>(
            @JsonProperty("data") T data
    ) {
    }
}
