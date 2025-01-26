package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Category {
    LANDSCAPE("풍경"),
    PORTRAIT("인물"),
    ANIMAL("동물"),
    PLANT("식물"),
    ARCHITECTURE("건축"),
    TRAVEL("여행"),
    FOOD("음식"),
    SPORTS("스포츠"),
    BLACK_AND_WHITE("흑백"),
    NIGHT_VIEW("야경"),
    STREET("길거리"),
    ABSTRACT("추상"),
    EVENT("이벤트"),
    FASHION("패션");


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
