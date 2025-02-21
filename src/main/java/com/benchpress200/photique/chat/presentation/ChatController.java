package com.benchpress200.photique.chat.presentation;

import com.benchpress200.photique.chat.application.ChatService;
import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{exhibitionId}")  // pub/~~ 으로 메시지 발행
    public void sendMessage(
            @DestinationVariable final Long exhibitionId,
            final ChatSendRequest chatSendRequest
    ) {
        chatSendRequest.withExhibitionId(exhibitionId);
        ChatSendResponse chatSendResponse = chatService.sendMessage(chatSendRequest);

        // 모든 구독자에게 메시지 전송 (본인포함)
        messagingTemplate.convertAndSend("/sub/" + exhibitionId, chatSendResponse);
    }
}
