package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.common.dtovalidator.Image;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequest {
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character")
    private String password;

    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace")
    private String nickname;

    @Image
    private MultipartFile profileImage;

    @NotNull(message = "Profile image status must be specified")
    private boolean isProfileImageDefault;

    public boolean hasPassword() {
        return password != null;
    }

    public boolean hasNickname() {
        return nickname != null;
    }

    public boolean hasProfileImage() {
        return profileImage != null;
    }
}
