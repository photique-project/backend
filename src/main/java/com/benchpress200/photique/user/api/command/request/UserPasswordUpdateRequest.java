package com.benchpress200.photique.user.api.command.request;

import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @NotNull(message = "Invalid password")
    @Pattern(regexp = "^(?!\\s*$)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    private String password;

    public UserPasswordUpdateCommand toCommand(Long userId) {
        return UserPasswordUpdateCommand.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
