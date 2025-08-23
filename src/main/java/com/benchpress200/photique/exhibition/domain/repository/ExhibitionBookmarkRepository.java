package com.benchpress200.photique.exhibition.domain.repository;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionBookmarkRepository extends JpaRepository<ExhibitionBookmark, Long> {
    void deleteByExhibitionId(Long exhibitionId);

    boolean existsByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUser(User user);

    void deleteByExhibition(Exhibition exhibition);

    List<ExhibitionBookmark> findByUserId(Long userId);

    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    Page<ExhibitionBookmark> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT e.id FROM ExhibitionBookmark b JOIN b.exhibition e WHERE b.user.id = :userId AND e.id IN :exhibitionIds")
    List<Long> findBookmarkedExhibitionIdsByUserIdAndExhibitionIds(
            @Param("userId") long userId,
            @Param("exhibitionIds") List<Long> exhibitionIds
    );
}
