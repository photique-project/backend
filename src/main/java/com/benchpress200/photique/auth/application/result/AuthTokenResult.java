package com.benchpress200.photique.auth.application.result;

import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenResult {
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpiredTime;

    public static AuthTokenResult from(final AuthenticationTokens authenticationTokens) {
        return AuthTokenResult.builder()
                .accessToken(authenticationTokens.getAccessToken())
                .refreshToken(authenticationTokens.getRefreshToken())
                .refreshTokenExpiredTime(authenticationTokens.getRefreshTokenExpiredTime())
                .build();
    }
}
