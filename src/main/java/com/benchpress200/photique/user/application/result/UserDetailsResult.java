package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDetailsResult {
    private Long userId;
    private String nickname;
    private String introduction;
    private String profileImage;
    private Long singleWorkCount;
    private Long exhibitionCount;
    private Long followerCount;
    private Long followingCount;
    private LocalDateTime createdAt;
    private boolean isFollowing;

    public static UserDetailsResult of(
            final User user,
            final Long singleWorkCount,
            final Long exhibitionCount,
            final Long followerCount,
            final Long followingCount,
            final boolean isFollowing
    ) {
        Long userId = user.getId();
        String nickname = user.getNickname();
        String introduction = user.getIntroduction();
        String profileImage = user.getProfileImage();
        LocalDateTime createdAt = user.getCreatedAt();

        return UserDetailsResult.builder()
                .userId(userId)
                .nickname(nickname)
                .introduction(introduction)
                .profileImage(profileImage)
                .singleWorkCount(singleWorkCount)
                .exhibitionCount(exhibitionCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .createdAt(createdAt)
                .isFollowing(isFollowing)
                .build();
    }
}
