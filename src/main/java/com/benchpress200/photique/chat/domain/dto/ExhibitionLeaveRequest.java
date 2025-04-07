package com.benchpress200.photique.chat.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Builder
@Getter
public class ExhibitionLeaveRequest {
    private String sessionId;

    public static ExhibitionLeaveRequest from(final SessionDisconnectEvent sessionSubscribeEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());

        return ExhibitionLeaveRequest.builder()
                .sessionId(headerAccessor.getSessionId())
                .build();
    }
}
