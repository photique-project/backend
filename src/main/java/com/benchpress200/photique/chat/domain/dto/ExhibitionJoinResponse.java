package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionJoinResponse {
    private String id;
    private Long userId;
    private String nickname;
    private int activeUsers;

    public static ExhibitionJoinResponse of(
            String id,
            User user,
            int activeUsers
    ) {
        return ExhibitionJoinResponse.builder()
                .id(id)
                .userId(user.getId())
                .nickname(user.getNickname())
                .activeUsers(activeUsers)
                .build();
    }


}
