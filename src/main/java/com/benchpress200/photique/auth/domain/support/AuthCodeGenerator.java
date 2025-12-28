package com.benchpress200.photique.auth.domain.support;

import java.util.concurrent.ThreadLocalRandom;

public class AuthCodeGenerator {
    private static final int CODE_LENGTH = 6;
    private static final int UPPER_BOUND = 1_000_000;
    private static final String CODE_FORMAT = "%0" + CODE_LENGTH + "d";

    public static String generate() {
        return String.format(CODE_FORMAT, ThreadLocalRandom.current().nextInt(UPPER_BOUND));
    }
}
