package com.benchpress200.photique.auth.domain.result;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class AuthenticationTokens {
    private final String accessToken;
    private final String refreshToken;
    private final Long refreshTokenExpiredTime;
}
