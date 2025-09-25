package com.benchpress200.photique.auth.domain;

import com.benchpress200.photique.auth.domain.model.IssueTokenResult;

public interface TokenManager {
    IssueTokenResult issueNewTokens(Long userId, boolean auto);

    boolean isExpired(String token);

    Long getUserId(String token);

    boolean isUnlimitedToken(String accessToken);
}
