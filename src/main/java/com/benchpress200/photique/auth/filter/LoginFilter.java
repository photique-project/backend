package com.benchpress200.photique.auth.filter;

import com.benchpress200.photique.auth.domain.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import com.benchpress200.photique.auth.exception.LoginRequestObjectReadException;
import com.benchpress200.photique.auth.filter.request.LoginRequest;
import com.benchpress200.photique.auth.filter.result.UserAuthenticationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;
    private final ObjectMapper objectMapper;

    // 로그인 엔드 포인터 요청 시 인증 시도
    @Override
    public Authentication attemptAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new LoginRequestObjectReadException(e.getMessage());
        }
    }

    // 로그인 성공 => 토큰 발급
    // 어세시 토큰: 바디
    // 리프레쉬 토큰: 쿠키
    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authentication
    ) throws IOException {
        UserAuthenticationResult userAuthenticationResult = (UserAuthenticationResult) authentication.getPrincipal();
        Long userId = userAuthenticationResult.getUserId();
        String role = userAuthenticationResult.getRole();
        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);
        // TODO: 어세스, 리프레시 발급 로직 구현할 차례

        // 공백을 위한 인코딩
        token = URLEncoder.encode(token, StandardCharsets.UTF_8);

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        AuthResponse authResponse = AuthResponse.builder()
                .status(HttpServletResponse.SC_OK)
                .message("Login completed successfully")
                .build();

        String jsonString = objectMapper.writeValueAsString(
                authResponse
        );

        response.getWriter().write(jsonString);
    }

    @Override
    protected void unsuccessfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException failed
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        AuthResponse authResponse = AuthResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Authentication failed")
                .build();

        String jsonString = objectMapper.writeValueAsString(authResponse);

        response.getWriter().write(jsonString);
    }
}
