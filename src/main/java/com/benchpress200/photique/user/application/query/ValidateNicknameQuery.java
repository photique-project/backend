package com.benchpress200.photique.user.application.query;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateNicknameQuery {
    private final String nickname;
}
