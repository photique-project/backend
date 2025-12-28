package com.benchpress200.photique.auth.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenRefreshCommand {
    private String refreshToken;

    public static AuthTokenRefreshCommand of(String refreshToken) {
        return AuthTokenRefreshCommand.builder()
                .refreshToken(refreshToken)
                .build();
    }
}
