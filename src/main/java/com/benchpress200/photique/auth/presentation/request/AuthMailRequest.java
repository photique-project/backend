package com.benchpress200.photique.auth.presentation.request;

import com.benchpress200.photique.auth.application.command.AuthMailCommand;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class AuthMailRequest {
    @Email(message = "Invalid email")
    private String email;

    public AuthMailCommand toCommand() {
        return AuthMailCommand.builder()
                .email(email)
                .build();
    }
}
