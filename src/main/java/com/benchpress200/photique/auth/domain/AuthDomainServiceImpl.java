package com.benchpress200.photique.auth.domain;

import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.entity.RefreshToken;
import com.benchpress200.photique.auth.domain.model.IssueTokenResult;
import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.AuthCodeRepository;
import com.benchpress200.photique.auth.infrastructure.AuthMailManager;
import com.benchpress200.photique.auth.infrastructure.RefreshTokenRepository;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainServiceImpl implements AuthDomainService {
    private static final String AUTH_COOKIE_KEY = "Authorization";

    private final TokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthMailManager authMailManager;
    private final AuthCodeRepository authCodeRepository;

    @Override
    public Long extractUserId(final String accessToken) {
        return tokenManager.getUserId(accessToken);
    }

    @Override
    public void validatePassword(
            final String loginPassword,
            final String password
    ) {
        if (!passwordEncoder.matches(loginPassword, password)) {
            throw new AuthException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Cookie issueToken(final User user, final boolean auto) {
        Long userId = user.getId();
        IssueTokenResult issueTokenResult = tokenManager.issueNewTokens(userId, auto);
        RefreshToken refreshToken = issueTokenResult.toRefreshTokenEntity();
        refreshTokenRepository.save(refreshToken);

        String accessToken = issueTokenResult.getAccessToken();

        return createAuthCookie(accessToken, true, auto);
    }

    @Override
    public Cookie expireToken(final String token) {
        if (token != null) {
            Long userId = tokenManager.getUserId(token);
            refreshTokenRepository.deleteByUserId(userId);
        }

        Cookie accessTokenCookie = new Cookie(AUTH_COOKIE_KEY, null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        return createAuthCookie(token, false, false);
    }

    @Override
    public void sendMail(final String email) {
        String authCode = authMailManager.sendMail(email);
        authCodeRepository.save(AuthCode.builder()
                .email(email)
                .code(authCode)
                .timeToLive(180L)
                .build());
    }

    @Override
    public void validateAuthMailCode(
            final String email,
            final String code
    ) {
        // 인증코드 찾기
        AuthCode authCode = authCodeRepository.findById(email)
                .orElseThrow(() -> new AuthException("Verification code has expired", HttpStatus.GONE));

        if (!authCode.getCode().equals(code)) {
            throw new AuthException("Invalid code", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public boolean isUnlimitedToken(final String accessToken) {
        return tokenManager.isUnlimitedToken(accessToken);
    }

    private Cookie createAuthCookie(
            final String token,
            final boolean issueToken,
            final boolean auto
    ) {
        Cookie accessTokenCookie = new Cookie(AUTH_COOKIE_KEY, token);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");

        if (issueToken) {
            if (auto) {
                accessTokenCookie.setMaxAge(60 * 60 * 24 * 365 * 10);
            } else {
                accessTokenCookie.setMaxAge(3600);
            }
        } else {
            accessTokenCookie.setMaxAge(0);
        }

        return accessTokenCookie;
    }
}
