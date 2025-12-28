package com.benchpress200.photique.user.application.query.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameValidateResult {
    private boolean isDuplicated;

    public static NicknameValidateResult of(boolean isDuplicated) {
        return new NicknameValidateResult(isDuplicated);
    }
}
