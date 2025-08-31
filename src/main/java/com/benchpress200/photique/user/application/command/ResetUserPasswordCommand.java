package com.benchpress200.photique.user.application.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResetUserPasswordCommand {
    private String email;
    private String password;
}
