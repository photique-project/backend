package com.benchpress200.photique.common.interceptor;

import com.benchpress200.photique.auth.domain.AuthDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final AuthDomainService authDomainService;
    private final UserDomainService userDomainService;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        // 핸들러 메서드가 아니라면 인증 필요없음
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // @Auth 어노테이션이 없다면 인증 필요없음
        if (!handlerMethod.getMethod().isAnnotationPresent(Auth.class)) {
            return true;
        }

        // 요청객체에서 어세스 토큰 조회
        String accessToken = authDomainService.findAccessToken(request);

        // 토큰에서 유저아이디 조회
        long userId = authDomainService.getUserIdFromToken(accessToken);
        User user = userDomainService.findUser(userId);

        // 자동 로그인 토큰이라면 토큰 재발급없이 통과
        if (authDomainService.isUnlimitedToken(accessToken)) {
            return true;
        }

        // 쿠키가 유효하다면,
        if (!authDomainService.isAccessTokenExpired(accessToken)) {
            Cookie cookie = authDomainService.issueToken(user, false); // 어세스 토큰과 리프레쉬 토큰 재발급
            response.addCookie(cookie);
            return true;
        }

        // 리프레쉬 토큰 만료여부 확인 -> 만료되었으면 예외처리됨
        authDomainService.isRefreshTokenPresent(userId);
        Cookie cookie = authDomainService.issueToken(user, false); // 어세스 토큰과 리프레쉬 토큰 재발급
        response.addCookie(cookie);

        return true;
    }
}
