package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Image;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Source;
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
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace")
    private String nickname;

    @Image
    private MultipartFile profileImage;

    public User toEntity(
            final String encryptedPassword,
            final String profileImageUrl
    ) {
        return User.builder()
                .email(email)
                .password(encryptedPassword)
                .nickname(nickname)
                .profileImage(profileImageUrl)
                .source(Source.LOCAL)
                .build();
    }

    public boolean hasProfileImage() {
        return profileImage != null;
    }
}
