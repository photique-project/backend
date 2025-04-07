package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.singlework.validation.annotation.Image;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {
    private Long userId;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character"
    )
    private String password;

    @Pattern(
            regexp = "^[^\\s]{1,11}$",
            message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace"
    )
    private String nickname;

    @Nullable
    @Size(max = 50, message = "The introduction must not exceed 50 characters")
    private String introduction;

    @Image
    private MultipartFile profileImage;

    public void withUserId(Long userId) {
        this.userId = userId;
    }
}
