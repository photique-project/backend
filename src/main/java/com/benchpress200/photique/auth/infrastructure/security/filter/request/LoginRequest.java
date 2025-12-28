package com.benchpress200.photique.auth.infrastructure.security.filter.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
