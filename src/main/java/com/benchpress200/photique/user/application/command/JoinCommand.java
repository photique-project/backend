package com.benchpress200.photique.user.application.command;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class JoinCommand {
    private String email;
    private String password;
    private String nickname;
    private MultipartFile profileImage;

    public User toEntity(
            final String password,
            final String profileImage,
            final Provider provider,
            final Role role
    ) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .provider(provider)
                .role(role)
                .build();
    }
}
