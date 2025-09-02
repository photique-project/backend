package com.benchpress200.photique.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ValidateNicknameRequest {
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname")
    @Schema(description = "1글자 이상 11글자 이하만 가능하며 공백을 포함할 수 없습니다.", example = "nickname")
    private String nickname;
}
