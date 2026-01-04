package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Getter;

@Getter
public class ExhibitionWriterResult {
    private final Long id;
    private final String nickname;
    private final String profileImage;
    private final String introduction;

    private ExhibitionWriterResult(User writer) {
        this.id = writer.getId();
        this.nickname = writer.getNickname();
        this.profileImage = writer.getProfileImage();
        this.introduction = writer.getIntroduction();
    }

    public static ExhibitionWriterResult from(User writer) {
        return new ExhibitionWriterResult(writer);
    }
}
