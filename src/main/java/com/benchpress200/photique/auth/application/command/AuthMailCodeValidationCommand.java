package com.benchpress200.photique.auth.application.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthMailCodeValidationCommand {
    private String email;

    private String code;

    public boolean validate(String code) {
        return this.code.equals(code);
    }
}
