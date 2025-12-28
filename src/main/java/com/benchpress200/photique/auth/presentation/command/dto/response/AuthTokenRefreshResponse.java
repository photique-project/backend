package com.benchpress200.photique.auth.presentation.command.dto.response;

import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseCookie;

@Getter
@Builder
public class AuthTokenRefreshResponse {
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final int MILLIS_PER_SECOND = 1000;

    private AccessTokenResponse accessTokenResponse;
    private ResponseCookie cookie;

    public static AuthTokenRefreshResponse from(AuthTokenResult authTokenResult) {
        String accessToken = authTokenResult.getAccessToken();
        String refreshToken = authTokenResult.getRefreshToken();
        int refreshTokenExpiredTime = (int) (authTokenResult.getRefreshTokenExpiredTime()
                / MILLIS_PER_SECOND); // 초 단위로 변환

        AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiredTime)
                .build();

        return AuthTokenRefreshResponse.builder()
                .accessTokenResponse(accessTokenResponse)
                .cookie(cookie)
                .build();
    }

    private record AccessTokenResponse(String accessToken) {
    }
}
