package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionBookmarkRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionBookmarkPersistenceAdapter implements
        ExhibitionBookmarkQueryPort,
        ExhibitionBookmarkCommandPort {

    private final ExhibitionBookmarkRepository exhibitionBookmarkRepository;

    @Override
    public boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId) {
        return exhibitionBookmarkRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds) {
        return exhibitionBookmarkRepository.findExhibitionIds(userId, exhibitionIds);
    }

    @Override
    public ExhibitionBookmark save(ExhibitionBookmark exhibitionBookmark) {
        return exhibitionBookmarkRepository.save(exhibitionBookmark);
    }
}
