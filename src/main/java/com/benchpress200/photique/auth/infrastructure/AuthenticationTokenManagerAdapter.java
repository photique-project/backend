package com.benchpress200.photique.auth.infrastructure;

import com.benchpress200.photique.auth.domain.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationTokenManagerAdapter implements AuthenticationTokenManagerPort {
    private static final String CLAIM_KEY = "UserId";

    @Value("${jwt.expired-time}")
    private Long expiredTime;

    @Value("${jwt.secret-key}")
    private String secret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }


    @Override
    public AuthenticationTokens issueTokens(
            final Long userId,
            final String role
    ) {
        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime))
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime))
                .signWith(secretKey)
                .compact();

        return AuthenticationTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public boolean isValid(String token) {
        return false;
    }

    @Override
    public Long getUserIdByToken(String token) {
        return 0L;
    }
}
