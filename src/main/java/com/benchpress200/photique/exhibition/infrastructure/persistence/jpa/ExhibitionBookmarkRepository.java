package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.id.ExhibitionBookmarkId;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionBookmarkRepository extends JpaRepository<ExhibitionBookmark, ExhibitionBookmarkId> {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    @Query("""
            SELECT b.exhibition.id
            FROM ExhibitionBookmark b
            WHERE b.user.id = :userId
            AND b.exhibition.id IN :exhibitionIds
            """)
    Set<Long> findExhibitionIds(
            @Param("userId") Long userId,
            @Param("exhibitionIds") List<Long> exhibitionIds
    );

    Optional<ExhibitionBookmark> findByUserAndExhibition(User user, Exhibition exhibition);

    @Query("""
            SELECT eb
            FROM ExhibitionBookmark eb
            JOIN FETCH eb.user
            JOIN FETCH eb.exhibition
            WHERE eb.user.id = :userId
            AND (
               :keyword IS NULL
               OR eb.exhibition.title LIKE CONCAT('%', :keyword, '%')
            )
            AND eb.exhibition.deletedAt IS NULL
            """)
    Page<ExhibitionBookmark> searchBookmarkedExhibitionByDeletedAtIsNull(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
