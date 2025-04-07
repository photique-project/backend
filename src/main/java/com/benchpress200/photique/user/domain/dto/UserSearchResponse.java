package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
    private Long id;
    private String profileImage;
    private String nickname;
    private String introduction;
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public static UserSearchResponse of(
            final UserSearch userSearch,
            final boolean isFollowing
    ) {
        return UserSearchResponse.builder()
                .id(userSearch.getId())
                .profileImage(userSearch.getProfileImage())
                .nickname(userSearch.getNickname())
                .introduction(userSearch.getIntroduction())
                .isFollowing(isFollowing)
                .build();
    }
}
