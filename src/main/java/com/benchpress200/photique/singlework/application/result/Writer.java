package com.benchpress200.photique.singlework.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;

@Builder
public record Writer(
        Long id,
        String nickname,
        String profileImage,
        String introduction
) {
    public static Writer from(User writer) {
        return Writer.builder()
                .id(writer.getId())
                .nickname(writer.getNickname())
                .profileImage(writer.getProfileImage())
                .introduction(writer.getIntroduction())
                .build();
    }

    public static Writer of(
            Long writerId,
            String nickname,
            String profileImage
    ) {
        return Writer.builder()
                .id(writerId)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }
}
