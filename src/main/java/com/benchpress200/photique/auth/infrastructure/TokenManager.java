package com.benchpress200.photique.auth.infrastructure;

import com.benchpress200.photique.auth.domain.dto.Tokens;
import io.jsonwebtoken.Jwts;

public interface TokenManager {
    Tokens issueNewTokens(Long userId);
    boolean isExpired(String token);
    Long getUserId(String token);
}
