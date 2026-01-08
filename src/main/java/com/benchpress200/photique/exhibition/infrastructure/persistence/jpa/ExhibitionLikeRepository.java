package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

    boolean existsByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUserAndExhibition(User user, Exhibition exhibition);

    void deleteByUser(User user);

    void deleteByExhibition(Exhibition exhibition);

    long countByExhibition(Exhibition exhibition);

    List<ExhibitionLike> findByUserId(Long userId);

    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    Page<ExhibitionLike> findByUserId(Long userId, Pageable pageable);

    @Query("""
            SELECT l.exhibition.id
            FROM ExhibitionLike l
            WHERE l.user.id = :userId
            AND l.exhibition.id IN :exhibitionIds
            """)
    Set<Long> findExhibitionIds(
            @Param("userId") Long userId,
            @Param("exhibitionIds") List<Long> exhibitionIds
    );

    Optional<ExhibitionLike> findByUserAndExhibition(User user, Exhibition exhibition);

    @Query("""
            SELECT el
            FROM ExhibitionLike el
            JOIN FETCH el.user
            JOIN FETCH el.exhibition
            WHERE el.user.id = :userId
            AND (
               :keyword IS NULL
               OR el.exhibition.title LIKE CONCAT('%', :keyword, '%')
            )
            AND el.exhibition.deletedAt IS NULL
            """)
    Page<ExhibitionLike> searchLikedExhibitionByDeletedAtIsNull(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
