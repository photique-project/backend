package com.benchpress200.photique.chat.application;

import com.benchpress200.photique.chat.domain.ChatDomainService;
import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveResponse;
import com.benchpress200.photique.chat.domain.entity.ExhibitionSession;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ExhibitionDomainService exhibitionDomainService;
    private final ChatDomainService chatDomainService;

    @Override
    public ChatSendResponse sendMessage(ChatSendRequest chatSendRequest) {
        // 메시지 종류확인
        String id = chatSendRequest.getId();

        // 유저확인
        Long userId = chatSendRequest.getUserId();
        User user = null;

        // 전시회 확인
        Long exhibitionId = chatSendRequest.getExhibitionId();
        exhibitionDomainService.findExhibition(exhibitionId);

        String content = chatSendRequest.getContent();
        return ChatSendResponse.of(id, user, content);
    }

    @Override
    public ExhibitionJoinResponse joinExhibition(ExhibitionJoinRequest exhibitionJoinRequest) {
        // 유저 존재확인
        Long userId = exhibitionJoinRequest.getUserId();
        User user = null;

        // 세션정보 저장
        ExhibitionSession exhibitionSession = exhibitionJoinRequest.toEntity();
        chatDomainService.joinExhibition(exhibitionSession);

        // 전시회 참여 유저수 조회
        Long exhibitionId = exhibitionSession.getExhibitionId();
        int activeUsers = chatDomainService.countActiveUsers(exhibitionId);

        // 참여 응답생성하고 반환
        return ExhibitionJoinResponse.of(
                "JOIN",
                user,
                activeUsers
        );

    }

    @Override
    public ExhibitionLeaveResponse leaveExhibition(ExhibitionLeaveRequest exhibitionLeaveRequest) {
        // 세션에 해당하는 레디스 엔티티 조회
        String sessionId = exhibitionLeaveRequest.getSessionId();
        ExhibitionSession exhibitionSession = chatDomainService.findExhibitionSession(sessionId);

        // 떠나는 유저 조회
        Long userId = exhibitionSession.getUserId();
        User user = null;

        // 전시회 참여 유저수 조회
        Long exhibitionId = exhibitionSession.getExhibitionId();
        int activeUsers = chatDomainService.countActiveUsers(exhibitionId);

        return ExhibitionLeaveResponse.of(
                "LEAVE",
                exhibitionId,
                user,
                activeUsers
        );
    }


}
