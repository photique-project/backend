package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Getter;

@Getter
public class Writer {
    private final Long id;
    private final String nickname;
    private final String profileImage;
    private String introduction;

    private Writer(User writer) {
        this.id = writer.getId();
        this.nickname = writer.getNickname();
        this.profileImage = writer.getProfileImage();
        this.introduction = writer.getIntroduction();
    }

    private Writer(
            Long id,
            String nickname,
            String profileImage
    ) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;

    }

    public static Writer from(User writer) {
        return new Writer(writer);
    }

    public static Writer of(
            Long id,
            String nickname,
            String profileImage
    ) {
        return new Writer(id, nickname, profileImage);
    }
}
