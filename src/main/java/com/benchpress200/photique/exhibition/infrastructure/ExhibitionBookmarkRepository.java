package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionBookmarkRepository extends JpaRepository<ExhibitionBookmark, Long> {
    void deleteByExhibitionId(Long exhibitionId);

    boolean existsByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUser(User user);

    void deleteByExhibition(Exhibition exhibition);
}
