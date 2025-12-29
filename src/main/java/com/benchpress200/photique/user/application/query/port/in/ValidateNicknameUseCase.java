package com.benchpress200.photique.user.application.query.port.in;

import com.benchpress200.photique.user.application.query.model.NicknameValidateQuery;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;

public interface ValidateNicknameUseCase {
    NicknameValidateResult validateNickname(NicknameValidateQuery query);
}
