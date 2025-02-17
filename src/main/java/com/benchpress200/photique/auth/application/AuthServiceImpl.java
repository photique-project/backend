package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.auth.domain.dto.AuthMailRequest;
import com.benchpress200.photique.auth.domain.dto.CodeValidationRequest;
import com.benchpress200.photique.auth.domain.dto.LoginRequest;
import com.benchpress200.photique.auth.domain.dto.Tokens;
import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.enumeration.AuthType;
import com.benchpress200.photique.auth.exception.AuthException;
import com.benchpress200.photique.auth.infrastructure.AuthCodeRepository;
import com.benchpress200.photique.auth.infrastructure.AuthMailManager;
import com.benchpress200.photique.auth.infrastructure.RefreshTokenRepository;
import com.benchpress200.photique.auth.infrastructure.TokenManager;
import com.benchpress200.photique.user.domain.dto.NicknameValidationRequest;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCodeRepository authCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenManager tokenManager;
    private final AuthMailManager authMailManager;

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

    @Override
    public Cookie logout(String token) {
        if (token != null) {
            Long userId = tokenManager.getUserId(token);
            refreshTokenRepository.deleteByUserId(userId);
        }

        return removeAccessTokenCookie();
    }

    @Override
    public void sendAuthMail(AuthMailRequest authMailRequest) {
        String email = authMailRequest.getEmail();

        if (authMailRequest.getType() == AuthType.JOIN) {
            userRepository.findByEmail(authMailRequest.getEmail())
                    .ifPresent(user -> {
                        throw new AuthException("This email address is already in use", HttpStatus.CONFLICT);
                    });
        } else {
            userRepository.findByEmail(authMailRequest.getEmail())
                    .orElseThrow(() -> new AuthException("User with email {" + email + "} is not found",
                            HttpStatus.NOT_FOUND));
        }

        String authCode = authMailManager.sendMail(email);
        authCodeRepository.save(AuthCode.builder()
                .email(email)
                .code(authCode)
                .timeToLive(180L)
                .build()
        );
    }

    @Override
    public void validateAuthMailCode(final CodeValidationRequest codeValidationRequest) {
        AuthCode authCode = authCodeRepository.findById(codeValidationRequest.getEmail())
                .orElseThrow(() -> new AuthException("Verification code has expired", HttpStatus.GONE));

        if (!codeValidationRequest.validate(authCode.getCode())) {
            throw new AuthException("Invalid code", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void validateNickname(NicknameValidationRequest nicknameValidationRequest) {
        String nickname = nicknameValidationRequest.getNickname();
        userRepository.findByNickname(nickname).ifPresent(
                user -> {
                    throw new AuthException(nickname + "is already in use", HttpStatus.CONFLICT);
                }
        );
    }

    private Cookie createAccessTokenCookie(String accessToken) {
        Cookie accessTokenCookie = new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        return accessTokenCookie;
    }

    private Cookie removeAccessTokenCookie() {
        Cookie accessTokenCookie = new Cookie("Authorization", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        return accessTokenCookie;
    }
}
