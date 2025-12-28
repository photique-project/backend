package com.benchpress200.photique.util;

import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
import com.benchpress200.photique.user.domain.enumeration.Role;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.http.entity.ContentType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class DummyGenerator {
    private static final String PAGE = "0";
    private static final String SIZE = "30";
    private static final String DUMMY_NEGATIVE_NUMBER = "-1";
    private static final String DUMMY_STRING = "a";
    private static final long DUMMY_INTEGER = 1;
    private static final String HYPHEN = "-";
    private static final String EMPTY = "";
    private static final String EMAIL_SEPARATOR = "@";
    private static final String DUMMY_DOMAIN = "example.com";
    private static final int LOCAL_PART_MAX_LENGTH = 8;
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+[]{};:,.<>?";
    private static final String ALL = LETTERS + DIGITS + SPECIALS;
    private static final int NICKNAME_MIN_LENGTH = 1;
    private static final int NICKNAME_MAX_LENGTH = 11;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int INTRODUCTION_MAX_LENGTH = 50;
    private static final String DUMMY_PROFILE_IMAGE_NAME = "test.png";
    private static final byte[] DUMMY_PROFILE_IMAGE_BYTES = "dummy".getBytes();
    private static final String NO_FILENAME = "";
    private static final String DUMMY_TLD = ".com";
    private static final String MULTIPART_KEY_PROFILE_IMAGE = "profileImage";
    private static final int MAX_PROFILE_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final String DUMMY_PNG_FILE_NAME = "dummy.png";
    private static final String DUMMY_TXT_FILE_NAME = "dummy.txt";


    public static String generateEmail() {
        String localPart = UUID.randomUUID().toString();
        localPart = localPart.replace(HYPHEN, EMPTY).substring(0, LOCAL_PART_MAX_LENGTH);

        return localPart + EMAIL_SEPARATOR + DUMMY_DOMAIN;
    }

    public static String generateNickname() {
        Random random = new Random();
        int length = random.nextInt(NICKNAME_MAX_LENGTH) + NICKNAME_MIN_LENGTH;

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

    public static NicknameValidateResult generateValidateNicknameResult(boolean result) {
        return NicknameValidateResult.of(result);
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

    public static long generatePathVariable() {
        return DUMMY_INTEGER;
    }

    public static String generateInvalidPathVariable() {
        return DUMMY_STRING;
    }

    public static MyDetailsResult generateMyDetailsResult(final long userId) {
        return MyDetailsResult.builder()
                .userId(userId)
                .build();
    }

    public static UserSearchResult generateUserSearchResult() {
        return UserSearchResult.builder().build();
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
        List<Character> chars = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        // 최소 조건 충족
        chars.add(LETTERS.charAt(random.nextInt(LETTERS.length())));   // 최소 하나의 문자
        chars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));    // 최소 하나의 숫자
        chars.add(SPECIALS.charAt(random.nextInt(SPECIALS.length())));  // 최소 하나의 특수문자

        // 나머지 (MIN_LENGTH - 3) 글자 랜덤 채우기
        for (int i = chars.size(); i < PASSWORD_MIN_LENGTH; i++) {
            chars.add(ALL.charAt(random.nextInt(ALL.length())));
        }

        // 랜덤 셔플로 순서 뒤섞기
        Collections.shuffle(chars, random);

        // 문자열로 변환
        StringBuilder sb = new StringBuilder();

        for (char c : chars) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static MockMultipartFile generateMockProfileImage(final String key) {
        return new MockMultipartFile(
                key,
                DUMMY_PROFILE_IMAGE_NAME,
                MediaType.IMAGE_PNG_VALUE,
                DUMMY_PROFILE_IMAGE_BYTES
        );

    }

    public static MockMultipartFile generateMockUserJson(
            final String key,
            final String jsonBody
    ) {
        return new MockMultipartFile(
                key,
                NO_FILENAME,
                MediaType.APPLICATION_JSON_VALUE,
                jsonBody.getBytes()
        );
    }

    public static List<String> generateInvalidPasswords() {
        SecureRandom random = new SecureRandom();
        char randomChar = LETTERS.charAt(random.nextInt(LETTERS.length()));
        char randomDigit = DIGITS.charAt(random.nextInt(DIGITS.length()));
        char randomSpecial = SPECIALS.charAt(random.nextInt(SPECIALS.length()));

        return List.of(
                String.valueOf(new char[0]),                                              // 빈 문자열
                String.valueOf((char) 32),                                                // 공백 문자열
                String.valueOf(randomChar).repeat(PASSWORD_MIN_LENGTH),                   // 문자만 (문자 반복)
                String.valueOf(randomDigit).repeat(PASSWORD_MIN_LENGTH),                  // 숫자만
                String.valueOf(randomSpecial).repeat(PASSWORD_MIN_LENGTH),                // 특수문자만
                new String(new char[]{randomChar, randomDigit, randomSpecial}).repeat(PASSWORD_MIN_LENGTH)
                        .substring(0, PASSWORD_MIN_LENGTH - 3),  // 길이 부족 (5글자)
                new String(new char[]{randomChar, randomDigit}).repeat(PASSWORD_MIN_LENGTH)
                        .substring(0, PASSWORD_MIN_LENGTH),       // 특수문자 미포함
                new String(new char[]{randomDigit, randomSpecial}).repeat(PASSWORD_MIN_LENGTH)
                        .substring(0, PASSWORD_MIN_LENGTH),       // 문자 미포함
                new String(new char[]{randomChar, randomSpecial}).repeat(PASSWORD_MIN_LENGTH)
                        .substring(0, PASSWORD_MIN_LENGTH)        // 숫자 미포함
        );
    }

    public static List<String> generateInvalidEmails() {
        SecureRandom random = new SecureRandom();
        char randomChar = LETTERS.charAt(random.nextInt(LETTERS.length()));
        String dummyLocalPart = String.valueOf(randomChar).repeat(LOCAL_PART_MAX_LENGTH);

        return List.of(
                String.valueOf(new char[0]),                                                      // 빈 문자열
                String.valueOf((char) 32),                                                        // 공백 문자열
                dummyLocalPart,                                                                   // 단순 문자열
                dummyLocalPart.concat(DUMMY_DOMAIN),                                              // @ 없음
                dummyLocalPart.concat(EMAIL_SEPARATOR),                                           // 도메인 없음
                dummyLocalPart.concat(EMAIL_SEPARATOR).concat(DUMMY_TLD),                         // 잘못된 도메인
                dummyLocalPart.concat(EMAIL_SEPARATOR).concat(dummyLocalPart)                     // TLD 없음
        );
    }

    public static List<MockMultipartFile> generateInvalidProfileImages() {
        return List.of(
                new MockMultipartFile(MULTIPART_KEY_PROFILE_IMAGE, DUMMY_PNG_FILE_NAME,
                        ContentType.IMAGE_PNG.getMimeType(), new byte[0]), // 빈 파일

                new MockMultipartFile(MULTIPART_KEY_PROFILE_IMAGE, DUMMY_PNG_FILE_NAME,
                        ContentType.IMAGE_PNG.getMimeType(),
                        new byte[MAX_PROFILE_IMAGE_SIZE + 1]), // 5MB 초과

                new MockMultipartFile(MULTIPART_KEY_PROFILE_IMAGE, DUMMY_TXT_FILE_NAME,
                        ContentType.TEXT_PLAIN.getMimeType(), new byte[MAX_PROFILE_IMAGE_SIZE]) // 확장자 틀림
        );
    }

    public static List<MockMultipartFile> generateInvalidProfileImagesWhenUpdate() {
        return List.of(
                new MockMultipartFile(MULTIPART_KEY_PROFILE_IMAGE, DUMMY_PNG_FILE_NAME,
                        ContentType.IMAGE_PNG.getMimeType(),
                        new byte[MAX_PROFILE_IMAGE_SIZE + 1]), // 5MB 초과

                new MockMultipartFile(MULTIPART_KEY_PROFILE_IMAGE, DUMMY_TXT_FILE_NAME,
                        ContentType.TEXT_PLAIN.getMimeType(), new byte[MAX_PROFILE_IMAGE_SIZE]) // 확장자 틀림
        );
    }

    public static String generateIntroduction() {
        SecureRandom random = new SecureRandom();

        return String.valueOf(CHARSET.charAt(random.nextInt(CHARSET.length()))).repeat(INTRODUCTION_MAX_LENGTH);
    }

    public static String generateInvalidIntroduction() {
        SecureRandom random = new SecureRandom();

        return String.valueOf(CHARSET.charAt(random.nextInt(CHARSET.length()))).repeat(INTRODUCTION_MAX_LENGTH + 1);
    }

    public static String generateRandomNumberString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }

        return ThreadLocalRandom.current()
                .ints(length, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public static Long generateResourceId() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static String generateRole() {
        return Role.USER.toString();
    }

    public static List<String> generateInvalidAuthMailCodes() {
        return List.of(
                String.valueOf(new char[0]), // 빈 문자열
                String.valueOf((char) 32));  // 공백 문자열
    }
}
