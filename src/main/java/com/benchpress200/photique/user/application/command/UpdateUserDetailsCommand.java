package com.benchpress200.photique.user.application.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UpdateUserDetailsCommand {
    private Long userId;
    private String nickname;
    private String introduction;
    private MultipartFile profileImage;
}
