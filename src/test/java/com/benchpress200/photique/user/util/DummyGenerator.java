package com.benchpress200.photique.user.util;

import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.SearchUsersResult;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DummyGenerator {
    private static final String PAGE = "0";
    private static final String SIZE = "30";
    private static final String DUMMY_NEGATIVE_NUMBER = "-1";
    private static final String DUMMY_STRING = "a";
    private static final String HYPHEN = "-";
    private static final String EMPTY = "";
    private static final String EMAIL_SEPARATOR = "@";
    private static final String DUMMY_DOMAIN = "example.com";
    private static final int LOCAL_PART_MAX_LENGTH = 8;
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 11;


    public static String generateEmail() {
        String localPart = UUID.randomUUID().toString();
        localPart = localPart.replace(HYPHEN, EMPTY).substring(0, LOCAL_PART_MAX_LENGTH);

        return localPart + EMAIL_SEPARATOR + DUMMY_DOMAIN;
    }

    public static String generateNickname() {
        Random random = new Random();
        int length = random.nextInt(MAX_LENGTH) + MIN_LENGTH;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }

        return sb.toString();
    }

    public static List<String> generateInvalidNicknames() {
        return List.of(
                String.valueOf(new char[0]), // 빈 문자열
                String.valueOf((char) 32), // 공백 문자열
                "a".repeat(13), // 12자 초과 문자열
                String.join(" ", "a", "b") // 공백 포함 문자열
        );
    }

    public static ValidateNicknameResult generateValidateNicknameResult(boolean result) {
        return ValidateNicknameResult.of(result);
    }

    public static UserDetailsResult generateUserDetailsResult(
            long userId
    ) {
        String nickname = DummyGenerator.generateNickname();
        long singleWorkCount = 0L;
        long exhibitionCount = 0L;
        long followerCount = 0L;
        long followingCount = 0L;
        boolean isFollowing = false;

        return UserDetailsResult.builder()
                .userId(userId)
                .nickname(nickname)
                .singleWorkCount(singleWorkCount)
                .exhibitionCount(exhibitionCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();
    }

    public static String generateInvalidPathVariable() {
        return DUMMY_STRING;
    }

    public static MyDetailsResult generateMyDetailsResult(long userId) {
        return MyDetailsResult.builder()
                .userId(userId)
                .build();
    }

    public static SearchUsersResult generateSearchUsersResult() {
        return SearchUsersResult.builder().build();
    }

    public static List<String> generateInvalidPages() {
        return List.of(
                DUMMY_NEGATIVE_NUMBER,
                DUMMY_STRING
        );
    }

    public static List<String> generateInvalidSizes() {
        return List.of(
                DUMMY_NEGATIVE_NUMBER,
                DUMMY_STRING
        );
    }

    public static String generatePage() {
        return PAGE;
    }

    public static String generateSize() {
        return SIZE;
    }

    public static String generatePassword() {
        return null;
    }
}
