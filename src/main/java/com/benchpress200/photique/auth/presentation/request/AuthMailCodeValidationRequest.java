package com.benchpress200.photique.auth.presentation.request;

import com.benchpress200.photique.auth.application.command.AuthMailCodeValidationCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthMailCodeValidationRequest {
    @NotNull(message = "Invalid email")
    @Email(
            message = "Invalid email",
            regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    private String email;

    @NotBlank(message = "Code for authentication must not be blank")
    private String code;

    public AuthMailCodeValidationCommand toCommand() {
        return AuthMailCodeValidationCommand.builder()
                .email(email)
                .code(code)
                .build();
    }
}
