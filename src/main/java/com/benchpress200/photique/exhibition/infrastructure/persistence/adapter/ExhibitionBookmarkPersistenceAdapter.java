package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionBookmarkPersistenceAdapter implements
        ExhibitionBookmarkQueryPort {

    private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;

    @Override
    public boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId) {
        return exhibitionBookmarkRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }
}
