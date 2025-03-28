package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerResponse {
    private Long id;
    private String nickname;
    private String introduction;
    private String profileImage;
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public static FollowerResponse of(
            final Follow follow,
            final boolean isFollowing
    ) {
        return FollowerResponse.builder()
                .id(follow.getFollower().getId())
                .nickname(follow.getFollower().getNickname())
                .introduction(follow.getFollower().getIntroduction())
                .profileImage(follow.getFollower().getProfileImage())
                .isFollowing(isFollowing)
                .build();
    }
}
