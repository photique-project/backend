package com.benchpress200.photique.user.domain.enumeration;

import lombok.Getter;

@Getter
public enum Source {
    LOCAL("LOCAL"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String source;

    Source(String source) {
        this.source = source;
    }
}
