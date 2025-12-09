package com.benchpress200.photique.auth.presentation.request;

import com.benchpress200.photique.auth.application.command.AuthMailCodeValidationCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthMailCodeValidationRequest {
    @Email(message = "Invalid email")
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
