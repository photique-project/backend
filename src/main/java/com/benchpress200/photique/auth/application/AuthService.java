package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import jakarta.servlet.http.Cookie;

public interface AuthService {
    Cookie login(LoginRequest loginRequest);
    Cookie logout(String token);
}
