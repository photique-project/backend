package com.benchpress200.photique.auth.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthMailCodeValidateCommand {
    private String email;

    private String code;

    public boolean validate(String code) {
        return this.code.equals(code);
    }
}
