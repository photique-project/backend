package com.benchpress200.photique.auth.filter.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginSuccessResponse {
    private final String accessToken;
}
