package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionLikeQueryPort {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds);

    Optional<ExhibitionLike> findByUserAndExhibition(User user, Exhibition exhibition);

    Page<ExhibitionLike> searchLikedExhibition(
            Long userId,
            String keyword,
            Pageable pageable
    );
}
