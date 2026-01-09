package com.benchpress200.photique.user.api.command.request;

import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordResetRequest {
    @NotNull(message = "Invalid email")
    @Email(message = "Invalid email", regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    @NotNull(message = "Invalid password")
    @Pattern(regexp = "^(?!\\s*$)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    private String password;

    public UserPasswordResetCommand toCommand() {
        return UserPasswordResetCommand.builder()
                .email(email)
                .password(password)
                .build();
    }
}
