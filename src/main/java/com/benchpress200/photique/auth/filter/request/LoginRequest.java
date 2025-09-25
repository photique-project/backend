package com.benchpress200.photique.auth.filter.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;
}
