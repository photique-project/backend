package com.benchpress200.photique.auth.filter;

import com.benchpress200.photique.auth.domain.port.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.auth.exception.LoginRequestObjectReadException;
import com.benchpress200.photique.auth.filter.request.LoginRequest;
import com.benchpress200.photique.auth.filter.response.LoginSuccessResponse;
import com.benchpress200.photique.common.response.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MimeTypeUtils;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final int MILLIS_PER_SECOND = 1000;

    private final AuthenticationManager authenticationManager;
    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;
    private final ObjectMapper objectMapper;

    // 로그인 엔드 포인터 요청 시 인증 시도
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
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

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException {
        AuthenticationUserResult authenticationUserResult = (AuthenticationUserResult) authentication.getPrincipal();
        Long userId = authenticationUserResult.getUserId();
        String role = authenticationUserResult.getRole();

        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);

        String accessToken = authenticationTokens.getAccessToken();
        String refreshToken = authenticationTokens.getRefreshToken();
        int refreshTokenExpiredTime = (int) (authenticationTokens.getRefreshTokenExpiredTime()
                / MILLIS_PER_SECOND); // 초 단위로 변환

        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshTokenExpiredTime);
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

        LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse(accessToken);
        ResponseBody<LoginSuccessResponse> responseBody = new ResponseBody<>(
                HttpServletResponse.SC_OK,
                "Login completed successfully",
                loginSuccessResponse,
                LocalDateTime.now()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonString);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);

        ResponseBody<?> responseBody = new ResponseBody<>(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication failed",
                null,
                LocalDateTime.now()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(jsonString);
    }
}
