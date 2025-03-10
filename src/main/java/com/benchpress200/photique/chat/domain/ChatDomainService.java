package com.benchpress200.photique.chat.domain;

import com.benchpress200.photique.chat.domain.entity.ExhibitionSession;

public interface ChatDomainService {
    Integer countActiveUsers(Long exhibitionId);

    void joinExhibition(ExhibitionSession exhibitionSession);

    ExhibitionSession findExhibitionSession(String sessionId);
}
