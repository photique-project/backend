package com.benchpress200.photique.user.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPasswordUpdateCommand {
    private Long userId;
    private String password;
}
