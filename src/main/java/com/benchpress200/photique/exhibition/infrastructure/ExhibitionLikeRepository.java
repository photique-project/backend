package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

    boolean existsByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUser(User user);

    void deleteByExhibition(Exhibition exhibition);

    Long countByExhibition(Exhibition exhibition);

    List<ExhibitionLike> findByUserId(Long userId);

    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);
}
