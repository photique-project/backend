package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionLikeRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionLikePersistenceAdapter implements
        ExhibitionLikeQueryPort {
    private final ExhibitionLikeRepository exhibitionLikeRepository;

    @Override
    public boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds) {
        return exhibitionLikeRepository.findExhibitionIds(userId, exhibitionIds);
    }
}
