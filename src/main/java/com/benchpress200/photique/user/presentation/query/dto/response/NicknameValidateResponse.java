package com.benchpress200.photique.user.presentation.query.dto.response;


import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NicknameValidateResponse {
    @JsonProperty("isDuplicated")
    private boolean isDuplicated;

    public static NicknameValidateResponse from(NicknameValidateResult validateNicknameResult) {
        boolean isDuplicated = validateNicknameResult.isDuplicated();
        return new NicknameValidateResponse(isDuplicated);
    }
}
