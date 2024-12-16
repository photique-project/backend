package com.benchpress200.photique.auth.domain.enumeration;

import lombok.Getter;

@Getter
public enum AuthType {
    JOIN("join"),
    RESET("reset");

    private final String value;

    AuthType(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (AuthType authType : AuthType.values()) {
            if (authType.getValue().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
