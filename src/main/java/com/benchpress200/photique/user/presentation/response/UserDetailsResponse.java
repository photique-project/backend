package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailsResponse {
    private Long userId;
    private String nickname;
    private String introduction;
    private String profileImage;
    private Long singleWorkCount;
    private Long exhibitionCount;
    private Long followerCount;
    private Long followingCount;
    private LocalDateTime createdAt;
    @JsonProperty("isFollowing")
    @Getter(AccessLevel.NONE)
    private boolean isFollowing;

    public static UserDetailsResponse from(UserDetailsResult userDetailsResult) {
        return UserDetailsResponse.builder()
                .userId(userDetailsResult.getUserId())
                .nickname(userDetailsResult.getNickname())
                .introduction(userDetailsResult.getIntroduction())
                .profileImage(userDetailsResult.getProfileImage())
                .singleWorkCount(userDetailsResult.getSingleWorkCount())
                .exhibitionCount(userDetailsResult.getExhibitionCount())
                .followerCount(userDetailsResult.getFollowerCount())
                .followingCount(userDetailsResult.getFollowingCount())
                .createdAt(userDetailsResult.getCreatedAt())
                .isFollowing(userDetailsResult.isFollowing())
                .build();
    }
}
