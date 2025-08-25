package com.benchpress200.photique.user.presentation.request;

import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.presentation.exception.InvalidProfileImageException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {
    @NotBlank(message = "Invalid email")
    @Email(message = "Invalid email")
    @Schema(description = "이메일 혐식을 따라야 합니다.", example = "test@example.com")
    private String email;

    @NotBlank(message = "Invalid password")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password")
    @Schema(description = "최소 8글자, 최소 하나의 문자, 최소 하나의 숫자, 최소 하나의 특수문자를 포함해야합니다.", example = "pasword12!@")
    private String password;

    @NotBlank(message = "Invalid nickname")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    @Schema(description = "1글자 이상 11글자 이하만 가능하며 공백을 포함할 수 없습니다.", example = "nickname")
    private String nickname;

    public JoinCommand toCommand(
            final MultipartFile profileImage
    ) {
        // 이미지 파일 검증 로직
        if (!isValidProfileImage(profileImage)) {
            throw new InvalidProfileImageException();
        }

        return JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

    private boolean isValidProfileImage(final MultipartFile file) {
        // null 가능
        if (file == null) {
            return true;
        }

        // 빈 파일 불가능
        if (file.isEmpty()) {
            return false;
        }

        // 사이즈 5MB 이하
        if (file.getSize() > 5 * 1024 * 1024) {
            return false;
        }

        String filename = file.getOriginalFilename();

        return filename != null && (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(
                ".png"));
    }
}
