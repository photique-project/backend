package com.benchpress200.photique.auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$", message = "Invalid password: Password must be at least 8 characters long, include at least one letter, one number, and one special character")
    private String password;

    private boolean auto;

    public void withAutoLogin(boolean auto) {
        this.auto = auto;
    }
}
