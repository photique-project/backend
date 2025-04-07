package com.benchpress200.photique.auth.application;


import com.benchpress200.photique.auth.domain.dto.AuthMailRequest;
import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.auth.domain.dto.WhoAmIResponse;
import jakarta.servlet.http.Cookie;

public interface AuthService {
    Cookie login(LoginRequest loginRequest);

    Cookie logout(String token);

    void sendJoinAuthMail(AuthMailRequest authMailRequest);

    void sendPasswordAuthMail(AuthMailRequest authMailRequest);

    void validateAuthMailCode(CodeValidationRequest codeValidationRequest);

    WhoAmIResponse whoAmI(String accessToken);
}
