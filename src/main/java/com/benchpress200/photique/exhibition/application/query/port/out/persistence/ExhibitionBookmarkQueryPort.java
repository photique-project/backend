package com.benchpress200.photique.exhibition.application.query.port.out.persistence;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionBookmarkQueryPort {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds);

    Optional<ExhibitionBookmark> findByUserAndExhibition(User user, Exhibition exhibition);

    Page<ExhibitionBookmark> searchBookmarkedExhibitionByDeletedAtIsNull(
            Long userId,
            String keyword,
            Pageable pageable
    );
}
