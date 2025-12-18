package com.benchpress200.photique.chat.presentation;

import com.benchpress200.photique.chat.application.ChatService;
import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{exhibitionId}")  // pub/~~ 으로 메시지 발행
    public void sendMessage(
            @DestinationVariable Long exhibitionId,
            ChatSendRequest chatSendRequest
    ) {
        chatSendRequest.withExhibitionId(exhibitionId);
        ChatSendResponse chatSendResponse = chatService.sendMessage(chatSendRequest);

        // 모든 구독자에게 메시지 전송 (본인포함)
        messagingTemplate.convertAndSend("/sub/" + exhibitionId, chatSendResponse);
    }

    // 구독 요청 시 해당 세선 id에 매핑되는 유저정보와 구독정보 레디스에 저장하고 입장 메시지 브로드캐스트
    @EventListener
    public void joinExhibition(SessionSubscribeEvent event) {
        ExhibitionJoinRequest exhibitionJoinRequest = ExhibitionJoinRequest.from(event);
        ExhibitionJoinResponse exhibitionJoinResponse = chatService.joinExhibition(exhibitionJoinRequest);
        Long exhibitionId = exhibitionJoinRequest.getExhibitionId();

        messagingTemplate.convertAndSend("/sub/" + exhibitionId, exhibitionJoinResponse);
    }

    // 전시회를 떠난다면 레디스에 저장했던 세선 정보 제거하고 퇴장 메시지 브로드 캐스트
    @EventListener
    public void leaveExhibition(SessionDisconnectEvent event) {
        ExhibitionLeaveRequest exhibitionLeaveRequest = ExhibitionLeaveRequest.from(event);
        ExhibitionLeaveResponse exhibitionLeaveResponse = chatService.leaveExhibition(exhibitionLeaveRequest);
        Long exhibitionId = exhibitionLeaveResponse.getExhibitionId();

        messagingTemplate.convertAndSend("/sub/" + exhibitionId, exhibitionLeaveResponse);
    }
}
