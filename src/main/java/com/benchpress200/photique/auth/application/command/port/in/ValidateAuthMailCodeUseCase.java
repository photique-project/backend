package com.benchpress200.photique.auth.application.command.port.in;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;

public interface ValidateAuthMailCodeUseCase {
    AuthMailCodeValidateResult validateAuthMailCode(AuthMailCodeValidateCommand command);
}
