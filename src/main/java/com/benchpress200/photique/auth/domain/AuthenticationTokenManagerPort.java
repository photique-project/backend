package com.benchpress200.photique.auth.domain;

import com.benchpress200.photique.auth.domain.result.AuthenticationTokens;

public interface AuthenticationTokenManagerPort {
    AuthenticationTokens issueTokens(Long userId, String role);

    boolean isValid(String token);

    Long getUserIdByToken(String token);
}
