package com.benchpress200.photique.auth.domain.result;

import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenValidationResult {
    private final TokenValidationStatus status;
    private Long userId;
    private String role;

    public TokenValidationResult(TokenValidationStatus status) {
        this.status = status;
    }
}
