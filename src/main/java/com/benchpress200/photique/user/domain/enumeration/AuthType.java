package com.benchpress200.photique.user.domain.enumeration;

import lombok.Getter;

@Getter
public enum AuthType {
    LOCAL("LOCAL"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String type;

    AuthType(String type) {
        this.type = type;
    }
}
