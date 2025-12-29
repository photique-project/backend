package com.benchpress200.photique.user.application.command.port.in;

import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;

public interface UpdateUserPasswordUseCase {
    void updateUserPassword(UserPasswordUpdateCommand command);
}
