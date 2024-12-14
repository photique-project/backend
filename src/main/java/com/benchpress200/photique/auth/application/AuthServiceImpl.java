package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.auth.domain.dto.Tokens;
import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.RefreshTokenRepository;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;

    @Override
    public Cookie login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthException("Authentication failed", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }

        Long userId = user.getId();
        Tokens tokens = tokenManager.issueNewTokens(userId);
        refreshTokenRepository.save(tokens.toRefreshTokenEntity());

        return createAccessTokenCookie(tokens.getAccessToken());
    }

    private Cookie createAccessTokenCookie(String accessToken) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        return accessTokenCookie;
    }
}
