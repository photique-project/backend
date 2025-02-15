package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {
    void deleteByExhibitionId(Long exhibitionId);

    boolean existsByUserAndExhibition(User user, Exhibition exhibition);
}
