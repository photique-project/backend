package com.benchpress200.photique.auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CodeValidationRequest {
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "The code must not be blank")
    private String code;

    public boolean validate(String code) {
        return this.code.equals(code);
    }
}
