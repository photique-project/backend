package com.benchpress200.photique.user.api.command.request;

import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordUpdateRequest {
    @NotNull(message = "Invalid password")
    @Pattern(regexp = "^(?!\\s*$)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    @Schema(description = "최소 8글자, 최소 하나의 문자, 최소 하나의 숫자, 최소 하나의 특수문자를 포함해야합니다.", example = "pasword12!@")
    private String password;

    public UserPasswordUpdateCommand toCommand(Long userId) {
        return UserPasswordUpdateCommand.builder()
                .userId(userId)
                .password(password)
                .build();
    }
}
