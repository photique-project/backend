package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.Follow;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowingResponse {
    private Long id;
    private String nickname;
    private String introduction;
    @JsonProperty("isFollowing")
    private boolean isFollowing;
    private String profileImage;

    public static FollowingResponse from(final Follow follow) {
        return FollowingResponse.builder()
                .id(follow.getFollowing().getId())
                .nickname(follow.getFollowing().getNickname())
                .introduction(follow.getFollowing().getIntroduction())
                .profileImage(follow.getFollowing().getProfileImage())
                .isFollowing(true)
                .build();
    }
}
