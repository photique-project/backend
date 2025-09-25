package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchedUser {
    private Long id;
    private String profileImage;
    private String nickname;
    private String introduction;
    @JsonProperty("isFollowing")
    @Getter(AccessLevel.NONE)
    private boolean isFollowing;

    public static SearchedUser of(
            final User user,
            final boolean isFollowing
    ) {
        Long id = user.getId();
        String profileImage = user.getProfileImage();
        String nickname = user.getNickname();
        String introduction = user.getIntroduction();

        return SearchedUser.builder()
                .id(id)
                .profileImage(profileImage)
                .nickname(nickname)
                .introduction(introduction)
                .isFollowing(isFollowing)
                .build();
    }
}
