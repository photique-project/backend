package com.benchpress200.photique.auth.domain.result;

import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenValidationResult {
    private TokenValidationStatus status;
    private Long userId;
    private String role;
}
