package com.benchpress200.photique.auth.domain.dto;

import com.benchpress200.photique.auth.domain.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Tokens {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenTimeToLive;

    public RefreshToken toRefreshTokenEntity() {
        return RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .timeToLive(refreshTokenTimeToLive)
                .build();
    }
}
