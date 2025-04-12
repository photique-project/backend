package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private String profileImage;
    private Long singleWork;
    private Long exhibition;
    private Long follower;
    private Long following;
    private LocalDateTime createdAt;
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public static UserDetailResponse of(
            final User user,
            final Long singleWork,
            final Long exhibition,
            final Long follower,
            final Long following,
            final boolean isFollowing
    ) {
        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .profileImage(user.getProfileImage())
                .singleWork(singleWork)
                .exhibition(exhibition)
                .follower(follower)
                .following(following)
                .createdAt(user.getCreatedAt())
                .isFollowing(isFollowing)
                .build();
    }
}
