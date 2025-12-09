package com.benchpress200.photique.auth.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthMailCodeValidationResult {
    private boolean success;

    public static AuthMailCodeValidationResult of(final boolean success) {
        return new AuthMailCodeValidationResult(success);
    }
}
