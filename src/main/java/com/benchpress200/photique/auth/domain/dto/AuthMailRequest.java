package com.benchpress200.photique.auth.domain.dto;

import com.benchpress200.photique.auth.domain.enumeration.AuthType;
import com.benchpress200.photique.common.dtovalidator.annotation.ValidAuthType;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class AuthMailRequest {
    @Email(message = "Invalid email")
    private String email;

    @ValidAuthType
    private AuthType type;
}
