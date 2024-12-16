package com.benchpress200.photique.auth.domain.entity;


import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@RedisHash(value = "refresh_token")
public class RefreshToken {
    @Id
    private Long userId;
    private String refreshToken;
    private LocalDateTime createdAt;

    @TimeToLive
    private long timeToLive;

    @Builder
    public RefreshToken(
            Long userId,
            String refreshToken,
            Long timeToLive
    ) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.timeToLive = timeToLive;
        createdAt = LocalDateTime.now();
    }
}
