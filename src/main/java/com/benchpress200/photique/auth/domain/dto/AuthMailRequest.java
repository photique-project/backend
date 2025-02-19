package com.benchpress200.photique.auth.domain.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class AuthMailRequest {
    @Email(message = "Invalid email")
    private String email;
}
