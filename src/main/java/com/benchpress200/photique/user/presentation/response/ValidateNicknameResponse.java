package com.benchpress200.photique.user.presentation.response;


import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidateNicknameResponse {
    @JsonProperty("isDuplicated")
    private boolean isDuplicated;

    public static ValidateNicknameResponse from(ValidateNicknameResult validateNicknameResult) {
        boolean isDuplicated = validateNicknameResult.isDuplicated();
        return new ValidateNicknameResponse(isDuplicated);
    }
}
