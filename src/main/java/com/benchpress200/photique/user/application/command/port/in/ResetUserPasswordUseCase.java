package com.benchpress200.photique.user.application.command.port.in;

import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;

public interface ResetUserPasswordUseCase {
    void resetUserPassword(UserPasswordResetCommand command);
}
