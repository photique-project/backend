package com.benchpress200.photique.auth.domain;

import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthDomainService {
    Long extractUserId(String accessToken);

    void validatePassword(String loginPassword, String password);

    Cookie issueToken(User user, boolean auto);

    Cookie expireToken(String token);

    void sendMail(String email);

    void validateAuthMailCode(String email, String code);

    boolean isUnlimitedToken(String accessToken);

    void isValidUser(String email);

    String findAccessToken(HttpServletRequest request);

    long getUserIdFromToken(String accessToken);

    boolean isAccessTokenExpired(String accessToken);

    void isRefreshTokenPresent(long userId);
}
