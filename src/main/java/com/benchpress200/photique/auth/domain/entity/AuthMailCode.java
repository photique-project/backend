package com.benchpress200.photique.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "auth-mail-code")
public class AuthMailCode {
    private static final long DEFAULT_TIME_TO_LIVE = 180L;
    private static final long VERIFIED_SESSION_TTL = 600L;

    @Id
    private String email;
    private String code;
    private boolean isVerified;

    @TimeToLive
    private Long timeToLive;

    public void verify() {
        isVerified = true;
        timeToLive = VERIFIED_SESSION_TTL;
    }

    public static AuthMailCode of(
            String email,
            String code
    ) {
        return AuthMailCode.builder()
                .email(email)
                .code(code)
                .isVerified(false)
                .timeToLive(DEFAULT_TIME_TO_LIVE)
                .build();
    }
}
