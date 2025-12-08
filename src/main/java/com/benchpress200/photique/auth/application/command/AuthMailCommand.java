package com.benchpress200.photique.auth.application.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthMailCommand {
    private String email;
}
