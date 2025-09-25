package com.benchpress200.photique.chat.domain;

import com.benchpress200.photique.chat.domain.entity.ExhibitionSession;
import com.benchpress200.photique.chat.exception.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Service
@RequiredArgsConstructor
public class ChatDomainServiceImpl implements ChatDomainService {
    private final WebSocketMessageBrokerStats webSocketMessageBrokerStats;
    private final ExhibitionSessionRepository exhibitionSessionRepository;

    @Override
    public Integer countActiveUsers(final Long exhibitionId) {
        return webSocketMessageBrokerStats.getWebSocketSessionStats().getWebSocketSessions();
    }

    @Override
    public void joinExhibition(final ExhibitionSession exhibitionSession) {
        exhibitionSessionRepository.save(exhibitionSession);
    }

    @Override
    public ExhibitionSession findExhibitionSession(final String sessionId) {
        return exhibitionSessionRepository.findById(sessionId).orElseThrow(
                () -> new ChatException("Session with id [" + sessionId + "] is not found.")
        );
    }
}
