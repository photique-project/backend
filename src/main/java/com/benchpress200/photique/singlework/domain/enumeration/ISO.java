package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ISO {
    ISO_50("50"),
    ISO_100("100"),
    ISO_200("200"),
    ISO_400("400"),
    ISO_800("800"),
    ISO_1600("1600"),
    ISO_3200("3200"),
    ISO_6400("6400"),
    ISO_12800("12800"),
    ISO_25600("25600"),
    ISO_51200("51200"),
    ISO_102400("102400"),
    ISO_204800("204800");

    private final String value;

    ISO(String value) {
        this.value = value;
    }

    public static boolean isValid(String input) {
        return Arrays.stream(ISO.values())
                .anyMatch(iso -> iso.value.equals(input));
    }

    public static ISO from(String input) {

        return Arrays.stream(ISO.values())
                .filter(iso -> iso.value.equals(input))
                .findFirst()
                .orElse(null);
    }
}
