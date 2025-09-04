package com.benchpress200.photique.user.presentation.request;

import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.presentation.exception.InvalidProfileImageException;
import com.benchpress200.photique.user.presentation.validator.ProfileImageValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserDetailsRequest {
    private Long userId;

    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    @Schema(description = "1글자 이상 11글자 이하만 가능하며 공백을 포함할 수 없습니다.", example = "nickname")
    private String nickname;

    @Size(max = 50, message = "Invalid introduction")
    @Schema(description = "50글지 이하만 가능합니다.", example = "Hello")
    private String introduction;

    public UpdateUserDetailsCommand toCommand(
            final Long userId,
            final MultipartFile profileImage
    ) {
        // 이미지 파일 검증 로직
        // 유저 업데이트에서는 빈 파일이라면 기본값 설정을 취급
        if (!ProfileImageValidator.isValid(profileImage) && !profileImage.isEmpty()) {
            throw new InvalidProfileImageException();
        }

        return UpdateUserDetailsCommand.builder()
                .userId(userId)
                .nickname(nickname)
                .introduction(introduction)
                .profileImage(profileImage)
                .build();
    }
}
