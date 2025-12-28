package com.benchpress200.photique.auth.infrastructure.security.adapter;

import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.port.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.vo.TokenValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private static final String CLAIM_USER_ID_KEY = "userId";
    private static final String CLAIM_ROLE_KEY = "role";
    private static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_WEEK = 7;
    private static final int ACCESS_TOKEN_INTERVALS_PER_HOUR = 4;

    @Value("${jwt.expired-time}")
    private Long expiredTime; // 900,000ms, 15분

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
        long refreshTokenExpiredTime = expiredTime * ACCESS_TOKEN_INTERVALS_PER_HOUR * HOURS_PER_DAY * DAYS_PER_WEEK;

        String accessToken = Jwts.builder()
                .claim(CLAIM_USER_ID_KEY, userId)
                .claim(CLAIM_ROLE_KEY, role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime)) // 만료기간 15분
                .signWith(secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .claim(CLAIM_USER_ID_KEY, userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiredTime)) // 만료기간 7일
                .signWith(secretKey)
                .compact();

        return AuthenticationTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpiredTime(refreshTokenExpiredTime)
                .build();
    }

    @Override
    public TokenValidationResult validateToken(final String token) {
        try {
            Claims claims = Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = claims.get(CLAIM_USER_ID_KEY, Long.class);
            String role = claims.get(CLAIM_ROLE_KEY, String.class);

            return TokenValidationResult.builder()
                    .status(TokenValidationStatus.VALID)
                    .userId(userId)
                    .role(role)
                    .build();
        } catch (ExpiredJwtException e) {
            Long userId = (Long) e.getClaims().get(CLAIM_USER_ID_KEY);

            return TokenValidationResult.builder()
                    .status(TokenValidationStatus.EXPIRED)
                    .userId(userId)
                    .build();
        } catch (JwtException e) {
            return TokenValidationResult.builder()
                    .status(TokenValidationStatus.INVALID)
                    .build();
        }
    }
}
