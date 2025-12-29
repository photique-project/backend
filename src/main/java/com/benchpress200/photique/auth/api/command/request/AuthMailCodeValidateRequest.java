package com.benchpress200.photique.auth.api.command.request;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AuthMailCodeValidateRequest {
    @NotNull(message = "Invalid email")
    @Email(
            message = "Invalid email",
            regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    private String email;

    @NotBlank(message = "Code for authentication must not be blank")
    private String code;

    public AuthMailCodeValidateCommand toCommand() {
        return AuthMailCodeValidateCommand.builder()
                .email(email)
                .code(code)
                .build();
    }
}
