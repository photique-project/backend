package com.benchpress200.photique.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameValidationRequest {
    @NotBlank(message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace")
    @Pattern(regexp = "^[^\\s]{1,11}$", message = "Invalid nickname: Nickname must be between 1 and 11 characters long and cannot contain any whitespace")
    private String nickname;
}
