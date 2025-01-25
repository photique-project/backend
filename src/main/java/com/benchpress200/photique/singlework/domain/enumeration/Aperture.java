package com.benchpress200.photique.singlework.domain.enumeration;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Aperture {
    NONE("미입력"),
    F_0_7("f/0.7"),
    F_0_8("f/0.8"),
    F_0_85("f/0.85"),
    F_0_95("f/0.95"),
    F_1("f/1"),
    F_1_2("f/1.2"),
    F_1_4("f/1.4"),
    F_1_8("f/1.8"),
    F_2("f/2"),
    F_2_8("f/2.8"),
    F_3_2("f/3.2"),
    F_3_5("f/3.5"),
    F_4("f/4"),
    F_4_5("f/4.5"),
    F_5("f/5"),
    F_5_6("f/5.6"),
    F_6_3("f/6.3"),
    F_7_1("f/7.1"),
    F_8("f/8"),
    F_9("f/9"),
    F_10("f/10"),
    F_11("f/11"),
    F_13("f/13"),
    F_14("f/14"),
    F_16("f/16"),
    F_22("f/22"),
    F_32("f/32"),
    F_40("f/40"),
    F_45("f/45"),
    F_64("f/64");

    private final String value;

    Aperture(String value) {
        this.value = value;
    }
    
    public static boolean isValid(String input) {
        return Arrays.stream(Aperture.values())
                .anyMatch(aperture -> aperture.value.equals(input));
    }
}
