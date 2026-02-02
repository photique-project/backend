package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPayload {
    private final Long id;
    private final String nickname;
    private final String profileImage;

    public static UserPayload from(User user) {
        return UserPayload.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
