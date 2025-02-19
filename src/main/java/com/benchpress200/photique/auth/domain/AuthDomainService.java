package com.benchpress200.photique.auth.domain;

import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;

public interface AuthDomainService {
    Long extractUserId(String accessToken);

    void validatePassword(String loginPassword, String password);

    Cookie issueToken(User user);

    Cookie expireToken(String token);

    void sendMail(String email);

    void validateAuthMailCode(String email, String code);
}
