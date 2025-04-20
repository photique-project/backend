package com.benchpress200.photique.user.domain.dto;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String email;
    private String password;
}
