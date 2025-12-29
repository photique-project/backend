package com.benchpress200.photique.auth.application.command.port.in;

import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;

public interface SendJoinAuthMailUseCase {
    void sendJoinAuthMail(AuthMailCommand command);
}
