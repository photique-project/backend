package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatSendResponse {
    private String id;
    private Long userId;
    private String profileImage;
    private String nickname;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static ChatSendResponse of(
            String id,
            User user,
            String content
    ) {
        return ChatSendResponse.builder()
                .id(id)
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
