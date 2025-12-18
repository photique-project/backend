package com.benchpress200.photique.chat.application;

import com.benchpress200.photique.chat.domain.dto.ChatSendRequest;
import com.benchpress200.photique.chat.domain.dto.ChatSendResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionJoinResponse;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveRequest;
import com.benchpress200.photique.chat.domain.dto.ExhibitionLeaveResponse;

public interface ChatService {
    ChatSendResponse sendMessage(ChatSendRequest chatSendRequest);

    ExhibitionJoinResponse joinExhibition(ExhibitionJoinRequest exhibitionJoinRequest);

    ExhibitionLeaveResponse leaveExhibition(ExhibitionLeaveRequest exhibitionLeaveRequest);
}
