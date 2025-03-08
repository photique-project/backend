package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatSendResponse {
    private Long userId;
    private String profileImage;
    private String nickname;
    private String content;
    private Long activeUsers;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static ChatSendResponse of(
            final User user,
            final String content,
            final Long activeUsers
    ) {
        return ChatSendResponse.builder()
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .content(content)
                .activeUsers(activeUsers)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
