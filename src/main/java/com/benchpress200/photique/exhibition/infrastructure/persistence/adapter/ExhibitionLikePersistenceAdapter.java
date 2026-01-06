package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionLikeRepository;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExhibitionLikePersistenceAdapter implements
        ExhibitionLikeQueryPort,
        ExhibitionLikeCommandPort {
    private final ExhibitionLikeRepository exhibitionLikeRepository;

    @Override
    public boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds) {
        return exhibitionLikeRepository.findExhibitionIds(userId, exhibitionIds);
    }

    @Override
    public Optional<ExhibitionLike> findByUserAndExhibition(User user, Exhibition exhibition) {
        return exhibitionLikeRepository.findByUserAndExhibition(user, exhibition);
    }

    @Override
    public Page<ExhibitionLike> searchLikedExhibition(Long userId, String keyword, Pageable pageable) {
        return exhibitionLikeRepository.searchLikedExhibition(userId, keyword, pageable);
    }

    @Override
    public ExhibitionLike save(ExhibitionLike exhibitionLike) {
        return exhibitionLikeRepository.save(exhibitionLike);
    }

    @Override
    public void delete(ExhibitionLike exhibitionLike) {
        exhibitionLikeRepository.delete(exhibitionLike);
    }
}
