package com.benchpress200.photique.chat.application;

import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;
import com.benchpress200.photique.exhibition.domain.ExhibitionDomainService;
import com.benchpress200.photique.user.domain.UserDomainService;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final UserDomainService userDomainService;
    private final ExhibitionDomainService exhibitionDomainService;

    @Override
    public ChatSendResponse sendMessage(final ChatSendRequest chatSendRequest) {
        // 유저확인
        Long userId = chatSendRequest.getUserId();
        User user = userDomainService.findUser(userId);

        // 전시회 확인
        Long exhibitionId = chatSendRequest.getExhibitionId();
        exhibitionDomainService.findExhibition(exhibitionId);

        String content = chatSendRequest.getContent();
        return ChatSendResponse.of(user, content);
    }
}
