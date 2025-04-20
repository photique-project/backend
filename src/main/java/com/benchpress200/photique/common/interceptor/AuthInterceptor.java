package com.benchpress200.photique.common.interceptor;

import com.benchpress200.photique.auth.domain.AuthDomainService;
import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.RefreshTokenRepository;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenManager tokenManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthDomainService authDomainService;
    private final UserDomainService userDomainService;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        // 핸들러 메서드가 아니라면
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // @Auth 어노테이션이 없다면
        if (!handlerMethod.getMethod().isAnnotationPresent(Auth.class)) {
            return true;
        }

        // TODO: 이후에 도메인 서비스로 처리 ㄱ
        String accessToken = findAccessToken(request).orElseThrow(
                () -> new AuthException(
                        "Authentication failed: cookie for authentication not found",
                        HttpStatus.UNAUTHORIZED)
        );

        URLDecoder.decode(accessToken, StandardCharsets.UTF_8);

        // 토큰에서 유저아이디 조회
        Long userId = tokenManager.getUserId(accessToken);
        User user = userDomainService.findUser(userId);

        boolean autoLogin = authDomainService.isUnlimitedToken(accessToken);

        if (autoLogin) {
            return true;
        }

        // 쿠키가 유효하다면,
        if (!tokenManager.isExpired(accessToken)) {
            Cookie cookie = authDomainService.issueToken(user, false); // 어세스 토큰과 리프레쉬 토큰 재발급
            response.addCookie(cookie);
            return true;
        }

        // 어세스토큰은 만료되고 리프레쉬 토큰은 살아있을 때
        refreshTokenRepository.findById(userId).orElseThrow(
                () -> new AuthException("Authentication failed: token is expired", HttpStatus.UNAUTHORIZED));
        Cookie cookie = authDomainService.issueToken(user, false); // 어세스 토큰과 리프레쉬 토큰 재발급
        response.addCookie(cookie);

        return true;
    }

    private Optional<String> findAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    private Cookie createAccessTokenCookie(String accessToken) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        return accessTokenCookie;
    }
}
