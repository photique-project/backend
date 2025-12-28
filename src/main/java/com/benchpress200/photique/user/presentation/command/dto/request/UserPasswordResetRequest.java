package com.benchpress200.photique.user.presentation.command.dto.request;

import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserPasswordResetRequest {
    @NotNull(message = "Invalid email")
    @Email(message = "Invalid email", regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Schema(description = "이메일 혐식을 따라야 합니다.", example = "test@example.com")
    private String email;

    @NotNull(message = "Invalid password")
    @Pattern(regexp = "^(?!\\s*$)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    @Schema(description = "최소 8글자, 최소 하나의 문자, 최소 하나의 숫자, 최소 하나의 특수문자를 포함해야합니다.", example = "pasword12!@")
    private String password;

    public UserPasswordResetCommand toCommand() {
        return UserPasswordResetCommand.builder()
                .email(email)
                .password(password)
                .build();
    }
}
