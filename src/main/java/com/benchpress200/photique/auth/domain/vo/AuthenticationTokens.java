package com.benchpress200.photique.auth.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticationTokens {
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpiredTime;
}
