package com.benchpress200.photique.user.application.command.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UserDetailsUpdateCommand {
    private Long userId;
    private String nickname;
    private String introduction;
    private MultipartFile profileImage;
}
