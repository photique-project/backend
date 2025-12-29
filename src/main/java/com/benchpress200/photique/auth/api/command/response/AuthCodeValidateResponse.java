package com.benchpress200.photique.auth.api.command.response;

import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthCodeValidateResponse {
    private boolean success;

    public static AuthCodeValidateResponse from(AuthMailCodeValidateResult result) {
        boolean success = result.isSuccess();
        return new AuthCodeValidateResponse(success);
    }
}
