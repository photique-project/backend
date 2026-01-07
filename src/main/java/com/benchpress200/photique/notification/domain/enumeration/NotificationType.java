package com.benchpress200.photique.notification.domain.enumeration;


import java.util.Arrays;
import lombok.Getter;

@Getter
public enum NotificationType {
    SINGLEWORK_COMMENT("singleworkComment"),
    EXHIBITION_COMMENT("exhibitionComment"),
    SINGLEWORK_LIKE("singleWorkLike"),
    EXHIBITION_LIKE("exhibitionLike"),
    EXHIBITION_BOOKMARK("exhibitionBookmark"),
    FOLLOWING_SINGLEWORK("followingSinglework"),
    FOLLOWING_EXHIBITION("followingExhibition"),
    FOLLOW("follow");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public static NotificationType from(String input) {
        return Arrays.stream(NotificationType.values())
                .filter(type -> type.value.equals(input))
                .findFirst()
                .orElse(null);
    }
}
