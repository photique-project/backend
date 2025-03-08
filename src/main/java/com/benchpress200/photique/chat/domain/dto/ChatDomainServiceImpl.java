package com.benchpress200.photique.chat.domain.dto;

import com.benchpress200.photique.chat.domain.ChatDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatDomainServiceImpl implements ChatDomainService {
    private final SimpUserRegistry simpUserRegistry;

    public Long countActiveUsers(final Long exhibitionId) {
        return simpUserRegistry.getUsers().stream()
                .flatMap(user -> user.getSessions().stream())
                .filter(session -> session.getSubscriptions().stream()
                        .anyMatch(sub -> sub.getDestination().equals("/sub/" + exhibitionId)))
                .count();
    }
}
