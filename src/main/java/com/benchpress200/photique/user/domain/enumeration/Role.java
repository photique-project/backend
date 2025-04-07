package com.benchpress200.photique.user.domain.enumeration;

import lombok.Getter;

@Getter
public enum Role {
    USER("LOCAL"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
