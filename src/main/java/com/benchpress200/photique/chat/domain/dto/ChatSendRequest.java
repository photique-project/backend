package com.benchpress200.photique.chat.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSendRequest {
    private String id;
    private Long userId;
    private Long exhibitionId;
    private String content;

    public void withExhibitionId(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }
}
