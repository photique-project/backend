package com.benchpress200.photique.notification.domain.enumeration;


import java.util.Arrays;
import lombok.Getter;

@Getter
public enum NotificationType {
    SINGLEWORK_CREATED("singleworkCreated"),
    SINGLEWORK_LIKED("singleWorkLiked"),
    SINGLEWORK_COMMENT_CREATED("singleworkCommentCreated"),

    EXHIBITION_CREATED("ExhibitionCreated"),
    EXHIBITION_LIKED("exhibitionLiked"),
    EXHIBITION_COMMENT_CREATED("exhibitionCommentCreated"),

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
