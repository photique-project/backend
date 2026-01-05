package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    Long countByWriter(User writer);

    @Query("""
            SELECT e
            FROM Exhibition e
            WHERE e.id = :id
            AND e.deletedAt IS NULL
            """)
    Optional<Exhibition> findActiveById(Long id);

    @Query("""
            SELECT e
            FROM Exhibition e
            JOIN FETCH e.writer
            WHERE e.id = :id
            AND e.deletedAt IS NULL
            """)
    Optional<Exhibition> findActiveByIdWithWriter(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Exhibition e
            SET e.viewCount = e.viewCount + 1
            WHERE e.id = :exhibitionId
            """)
    void incrementViewCount(@Param("exhibitionId") Long exhibitionId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Exhibition e
            SET e.likeCount = e.likeCount + 1
            WHERE e.id = :exhibitionId
            """)
    void incrementLikeCount(@Param("exhibitionId") Long exhibitionId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE Exhibition e
            SET e.likeCount = e.likeCount - 1
            WHERE e.id = :exhibitionId
            """)
    void decrementLikeCount(@Param("exhibitionId") Long exhibitionId);
}
