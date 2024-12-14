package com.benchpress200.photique.auth.domain.entity;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 3600 * 24 * 7)
public class RefreshToken {
    @Id
    private Long userId;
    private String refreshToken;
    private LocalDateTime createdAt;

    @Builder
    public RefreshToken(
            Long userId,
            String refreshToken
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        createdAt = LocalDateTime.now();
    }
}
