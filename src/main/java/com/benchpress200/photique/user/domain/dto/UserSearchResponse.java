package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
    private Long id;
    private String profileImage;
    private String nickname;

    public static UserSearchResponse from(final UserSearch userSearch) {
        return UserSearchResponse.builder()
                .id(userSearch.getId())
                .profileImage(userSearch.getProfileImage())
                .nickname(userSearch.getNickname())
                .build();
    }
}
