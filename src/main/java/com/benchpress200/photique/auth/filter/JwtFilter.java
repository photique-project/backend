package com.benchpress200.photique.auth.filter;

import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.port.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.auth.domain.result.TokenValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final String AUTHENTICATION_HEADER = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";
    private final int BEARER_PREFIX_LENGTH = 7;

    private final AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = extractToken(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        TokenValidationResult tokenValidationResult = authenticationTokenManagerPort.validateToken(accessToken);
        TokenValidationStatus status = tokenValidationResult.getStatus();

        // 토큰이 유효하다면 SecurityContextHolder에 인증 객체 담기
        if (status == TokenValidationStatus.VALID) {
            Long userId = tokenValidationResult.getUserId();
            String role = tokenValidationResult.getRole();

            AuthenticationUserResult authenticationUserResult = AuthenticationUserResult.builder()
                    .userId(userId)
                    .role(role)
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationUserResult, null,
                    authenticationUserResult.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(AUTHENTICATION_HEADER);

        if (authenticationHeader != null && authenticationHeader.startsWith(BEARER_PREFIX)) {
            return authenticationHeader.substring(BEARER_PREFIX_LENGTH);
        }

        return null;
    }
}
