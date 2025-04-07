package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionLeaveResponse {
    private String id;
    private Long exhibitionId;
    private Long userId;
    private String nickname;
    private int activeUsers;

    public static ExhibitionLeaveResponse of(
            final String id,
            final Long exhibitionId,
            final User user,
            final int activeUsers
    ) {
        return ExhibitionLeaveResponse.builder()
                .id(id)
                .exhibitionId(exhibitionId)
                .userId(user.getId())
                .nickname(user.getNickname())
                .activeUsers(activeUsers)
                .build();
    }

}
