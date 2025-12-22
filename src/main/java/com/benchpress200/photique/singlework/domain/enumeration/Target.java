package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Target {
    WORK("work"),
    WRITER("writer");

    private final String value;

    Target(String value) {
        this.value = value;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(Target.values())
                .anyMatch(target -> target.value.equals(input));
    }

    public static Target from(String input) {
        return Arrays.stream(Target.values())
                .filter(target -> target.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid target value: " + input));
    }
}
