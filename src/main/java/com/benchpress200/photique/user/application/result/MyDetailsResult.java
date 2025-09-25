package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyDetailsResult {
    private Long userId;
    private String email;
    private String nickname;
    private String introduction;
    private String profileImage;
    private Long singleWorkCount;
    private Long exhibitionCount;
    private Long followerCount;
    private Long followingCount;
    private LocalDateTime createdAt;

    public static MyDetailsResult of(
            final User user,
            final Long singleWorkCount,
            final Long exhibitionCount,
            final Long followerCount,
            final Long followingCount
    ) {
        Long userId = user.getId();
        String email = user.getEmail();
        String nickname = user.getNickname();
        String introduction = user.getIntroduction();
        String profileImage = user.getProfileImage();
        LocalDateTime createdAt = user.getCreatedAt();

        return MyDetailsResult.builder()
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .introduction(introduction)
                .profileImage(profileImage)
                .singleWorkCount(singleWorkCount)
                .exhibitionCount(exhibitionCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .createdAt(createdAt)
                .build();
    }
}
