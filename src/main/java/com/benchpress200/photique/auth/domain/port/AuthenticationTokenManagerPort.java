package com.benchpress200.photique.auth.domain.port;

import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.result.TokenValidationResult;

public interface AuthenticationTokenManagerPort {
    AuthenticationTokens issueTokens(Long userId, String role);

    TokenValidationResult validateToken(String token);
}
