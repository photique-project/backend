package com.benchpress200.photique.user.application.query.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameValidateQuery {
    private String nickname;
}
