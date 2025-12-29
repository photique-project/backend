package com.benchpress200.photique.user.api.command.request;

import com.benchpress200.photique.image.presentation.validator.ProfileImageValidator;
import com.benchpress200.photique.user.api.command.exception.InvalidProfileImageException;
import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ResisterRequest {
    @NotNull(message = "Invalid email")
    @Email(
            message = "Invalid email",
            regexp = "^(?!\\s*$)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    private String email;

    @NotNull(message = "Invalid password")
    @Pattern(regexp = "^(?!\\s*$)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    private String password;

    @NotNull(message = "Invalid nickname")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    private String nickname;

    public ResisterCommand toCommand(MultipartFile profileImage) {
        // 프로파일 이미지 파일 검증 로직
        if (!ProfileImageValidator.isValid(profileImage)) {
            throw new InvalidProfileImageException();
        }

        return ResisterCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
