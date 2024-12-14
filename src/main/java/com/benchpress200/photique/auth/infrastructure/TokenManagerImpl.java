package com.benchpress200.photique.auth.infrastructure;

import com.benchpress200.photique.auth.domain.dto.Tokens;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenManagerImpl implements TokenManager {
    private static final String TOKEN_PREFIX = "Bearer ";
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
    public Tokens issueNewTokens(Long userId) {
        String accessToken =  TOKEN_PREFIX + Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime))
                .signWith(secretKey)
                .compact();

        accessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

        String refreshToken = Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredTime * 24 * 7))
                .signWith(secretKey)
                .compact();

        return Tokens.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public boolean isExpired(String token) {
        token = URLDecoder.decode(token, StandardCharsets.UTF_8);

        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public Long getUserId(String token) {
        token = URLDecoder.decode(token, StandardCharsets.UTF_8);

        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.substring(TOKEN_PREFIX.length());
        }

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(CLAIM_KEY, Long.class);
    }
}
