package com.benchpress200.photique.user.domain.enumeration;

import lombok.Getter;

@Getter
public enum Provider {
    LOCAL("LOCAL"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String provider;

    Provider(final String provider) {
        this.provider = provider;
    }
}
