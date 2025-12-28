package com.benchpress200.photique.auth.infrastructure.security.filter.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Cookie tokenCookie = new Cookie(REFRESH_TOKEN_KEY, null);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);

        response.addCookie(tokenCookie);
    }
}
