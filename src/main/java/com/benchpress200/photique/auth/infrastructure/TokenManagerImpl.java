package com.benchpress200.photique.auth.infrastructure;

import com.benchpress200.photique.auth.domain.TokenManager;
import com.benchpress200.photique.auth.domain.model.IssueTokenResult;
import com.benchpress200.photique.auth.exception.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public IssueTokenResult issueNewTokens(final Long userId, final boolean auto) {
        long now = System.currentTimeMillis();

        JwtBuilder accessTokenBuilder = Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(now))
                .signWith(secretKey);

        // 자동 로그인 요청이라면 만료시간 설정 X
        if (!auto) {
            accessTokenBuilder.expiration(new Date(now + expiredTime));
        }

        String accessToken = accessTokenBuilder.compact();
        accessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

        String refreshToken = Jwts.builder()
                .claim(CLAIM_KEY, userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiredTime * 24 * 7))
                .signWith(secretKey)
                .compact();

        return IssueTokenResult.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenTimeToLive(60L * 60L * 24L * 7L)
                .build();
    }

    @Override
    public Long getUserId(String token) {
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
                    .get(CLAIM_KEY, Long.class);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 Claims 직접 꺼냄
            return e.getClaims().get(CLAIM_KEY, Long.class);
        } catch (JwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰일 경우
            throw new AuthException("Authentication failed: invalid token", HttpStatus.UNAUTHORIZED);
        }
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
            return true;
        }
    }

    @Override
    public boolean isUnlimitedToken(String accessToken) {
        String[] parts = accessToken.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        try {
            accessToken = URLDecoder.decode(accessToken, StandardCharsets.UTF_8);

            if (accessToken.startsWith(TOKEN_PREFIX)) {
                accessToken = accessToken.substring(TOKEN_PREFIX.length());
            }

            // 단순 서명 및 구조 유효성 검사 (만료 여부는 고려 X)
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(accessToken);

            return !payloadJson.contains("\"exp\"");
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            // 서명 불일치, 잘못된 포맷 등
            throw new AuthException("Authentication failed: invalid token", HttpStatus.UNAUTHORIZED);
        }
    }
}
