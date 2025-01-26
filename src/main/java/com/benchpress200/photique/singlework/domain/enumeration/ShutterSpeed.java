package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ShutterSpeed {
    NONE("미입력"),
    S_1_8000("1/8000"),
    S_1_4000("1/4000"),
    S_1_2000("1/2000"),
    S_1_1000("1/1000"),
    S_1_500("1/500"),
    S_1_250("1/250"),
    S_1_125("1/125"),
    S_1_60("1/60"),
    S_1_30("1/30"),
    S_1_15("1/15"),
    S_1_8("1/8"),
    S_1_4("1/4"),
    S_1_2("1/2"),
    S_1("1"),
    S_2("2"),
    S_4("4"),
    S_8("8"),
    S_15("15"),
    S_30("30");

    private final String value;

    ShutterSpeed(String value) {
        this.value = value;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(ShutterSpeed.values())
                .anyMatch(shutterSpeed -> shutterSpeed.value.equals(input));
    }

    public static ShutterSpeed fromValue(String input) {
        return Arrays.stream(ShutterSpeed.values())
                .filter(shutterSpeed -> shutterSpeed.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid shutterSpeed value: " + input));
    }
}
