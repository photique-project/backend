package com.benchpress200.photique.common.response;

import com.benchpress200.photique.common.response.ApiFailureResponse.FailureMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiFailureResponse extends ResponseEntity<FailureMessage> {
    public ApiFailureResponse(String message, HttpStatus status) {
        super(new FailureMessage(message), status);
    }
    public ApiFailureResponse(HttpStatus status) {
        super(status);
    }

    public record FailureMessage(
            String message
    ) {
    }
}
