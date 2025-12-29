package com.benchpress200.photique.user.application.command.port.in;

import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;

public interface UpdateUserDetailsUseCase {
    void updateUserDetails(UserDetailsUpdateCommand command);
}
