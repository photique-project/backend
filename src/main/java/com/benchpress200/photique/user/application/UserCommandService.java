package com.benchpress200.photique.user.application;

import com.benchpress200.photique.user.application.command.JoinCommand;

public interface UserCommandService {
    void join(JoinCommand joinCommand);
}
