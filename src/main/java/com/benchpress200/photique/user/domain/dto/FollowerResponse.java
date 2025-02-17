package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.Follow;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerResponse {
    private Long id;
    private String nickname;
    private String profileImage;

    public static FollowerResponse from(final Follow follow) {
        return FollowerResponse.builder()
                .id(follow.getFollower().getId())
                .nickname(follow.getFollower().getNickname())
                .profileImage(follow.getFollower().getProfileImage())
                .build();
    }
}
