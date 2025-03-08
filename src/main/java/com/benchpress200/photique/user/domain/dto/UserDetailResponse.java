package com.benchpress200.photique.user.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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

    public static UserDetailResponse of(
            final User user,
            final Long singleWork,
            final Long exhibition,
            final Long follower,
            final Long following
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
                .build();
    }
}
