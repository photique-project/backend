package com.benchpress200.photique.exhibition.infrastructure.persistence.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.infrastructure.persistence.jpa.ExhibitionBookmarkRepository;
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
    public Optional<ExhibitionBookmark> findByUserAndExhibition(User user, Exhibition exhibition) {
        return exhibitionBookmarkRepository.findByUserAndExhibition(user, exhibition);
    }

    @Override
    public Page<ExhibitionBookmark> searchBookmarkedExhibition(Long userId, String keyword, Pageable pageable) {
        return exhibitionBookmarkRepository.searchBookmarkedExhibition(
                userId,
                keyword,
                pageable
        );
    }

    @Override
    public ExhibitionBookmark save(ExhibitionBookmark exhibitionBookmark) {
        return exhibitionBookmarkRepository.save(exhibitionBookmark);
    }

    @Override
    public void delete(ExhibitionBookmark exhibitionBookmark) {
        exhibitionBookmarkRepository.delete(exhibitionBookmark);
    }
}
