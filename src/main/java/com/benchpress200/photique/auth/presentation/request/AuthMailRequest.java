package com.benchpress200.photique.auth.presentation.request;

import com.benchpress200.photique.auth.application.command.AuthMailCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthMailRequest {
    @NotNull(message = "Invalid email")
    @Email(
            message = "Invalid email",
            regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    private String email;

    public AuthMailCommand toCommand() {
        return AuthMailCommand.builder()
                .email(email)
                .build();
    }
}
