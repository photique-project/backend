package com.benchpress200.photique.chat.application;

import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;

public interface ChatService {
    ChatSendResponse sendMessage(ChatSendRequest chatSendRequest);
}
