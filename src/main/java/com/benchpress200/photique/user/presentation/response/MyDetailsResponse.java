package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.MyDetailsResult;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyDetailsResponse {
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

    public static MyDetailsResponse from(final MyDetailsResult myDetailsResult) {
        return MyDetailsResponse.builder()
                .userId(myDetailsResult.getUserId())
                .email(myDetailsResult.getEmail())
                .nickname(myDetailsResult.getNickname())
                .introduction(myDetailsResult.getIntroduction())
                .profileImage(myDetailsResult.getProfileImage())
                .singleWorkCount(myDetailsResult.getSingleWorkCount())
                .exhibitionCount(myDetailsResult.getExhibitionCount())
                .followerCount(myDetailsResult.getFollowerCount())
                .followingCount(myDetailsResult.getFollowingCount())
                .createdAt(myDetailsResult.getCreatedAt())
                .build();
    }
}
