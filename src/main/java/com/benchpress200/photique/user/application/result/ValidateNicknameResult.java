package com.benchpress200.photique.user.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidateNicknameResult {
    private boolean isDuplicated;

    public static ValidateNicknameResult of(boolean isDuplicated) {
        return new ValidateNicknameResult(isDuplicated);
    }
}
