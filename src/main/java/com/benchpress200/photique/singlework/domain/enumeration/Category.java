package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Category {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    ANIMAL("animal"),
    PLANT("plant"),
    ARCHITECTURE("architecture"),
    TRAVEL("travel"),
    FOOD("food"),
    SPORTS("sports"),
    BLACK_AND_WHITE("blackAndWhite"),
    NIGHTSCAPE("nightscape"),
    STREET("street"),
    ABSTRACT("abstract"),
    EVENT("event"),
    FASHION("fashion");


    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(Category.values())
                .anyMatch(category -> category.value.equals(input));
    }

    public static Category fromValue(String input) {

        return Arrays.stream(Category.values())
                .filter(category -> category.value.equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid category value: " + input));
    }
}
