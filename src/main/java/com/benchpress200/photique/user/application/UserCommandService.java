package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;

public interface UserCommandService {
    void join(JoinCommand joinCommand);

    void updateUserDetails(UpdateUserDetailsCommand updateUserDetailsCommand);
}
