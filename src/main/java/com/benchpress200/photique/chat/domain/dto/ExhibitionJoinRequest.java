package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.chat.domain.entity.ExhibitionSession;
import lombok.Builder;
import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;


@Builder
@Getter
public class ExhibitionJoinRequest {
    private String sessionId;
    private Long exhibitionId;
    private Long userId;

    public static ExhibitionJoinRequest from(final SessionSubscribeEvent sessionSubscribeEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());

        return ExhibitionJoinRequest.builder()
                .sessionId(headerAccessor.getSessionId())
                .exhibitionId(Long.parseLong(headerAccessor.getDestination().replace("/sub/", "")))
                .userId(Long.parseLong(headerAccessor.getFirstNativeHeader("userId")))
                .build();
    }

    public ExhibitionSession toEntity() {
        return ExhibitionSession.builder()
                .sessionId(sessionId)
                .userId(userId)
                .exhibitionId(exhibitionId)
                .timeToLive(60 * 60 * 24L)
                .build();
    }
}
