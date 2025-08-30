package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;

public interface UserCommandService {
    void join(JoinCommand joinCommand);

    void updateUserDetails(UpdateUserDetailsCommand updateUserDetailsCommand);

    void updateUserPassword(UpdateUserPasswordCommand updateUserPasswordCommand);
}
