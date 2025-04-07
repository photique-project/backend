package com.benchpress200.photique.notification.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Type {
    SINGLE_WORK_COMMENT("single_work_comment"),
    EXHIBITION_COMMENT("exhibition_comment"),
    SINGLE_WORK_LIKE("single_work_like"),
    EXHIBITION_LIKE("exhibition_like"),
    EXHIBITION_BOOKMARK("exhibition_bookmark"),
    FOLLOWING_SINGLE_WORK("following_single_work"),
    FOLLOWING_EXHIBITION("following_exhibition"),
    FOLLOW("follow");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public static Type fromValue(String input) {
        return Arrays.stream(Type.values())
                .filter(type -> type.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid type value: " + input));
    }
}
