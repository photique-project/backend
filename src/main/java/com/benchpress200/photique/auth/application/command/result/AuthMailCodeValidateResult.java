package com.benchpress200.photique.auth.application.command.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthMailCodeValidateResult {
    private boolean success;

    public static AuthMailCodeValidateResult of(boolean success) {
        return new AuthMailCodeValidateResult(success);
    }
}
