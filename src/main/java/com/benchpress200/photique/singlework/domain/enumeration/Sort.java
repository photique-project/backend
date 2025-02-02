package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Sort {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    ANIMAL("animal"),
    PLANT("plant"),
    ARCHITECTURE("architecture"),
    TRAVEL("travel"),
    FOOD("food"),
    SPORTS("sports"),
    BLACK_AND_WHITE("bw"),
    NIGHTSCAPE("nightscape"),
    STREET("street"),
    ABSTRACT("abstract"),
    EVENT("event"),
    FASHION("fashion");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(Sort.values())
                .anyMatch(sort -> sort.value.equals(input));
    }

    public static Sort fromValue(String input) {
        return Arrays.stream(Sort.values())
                .filter(sort -> sort.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort value: " + input));
    }
}
