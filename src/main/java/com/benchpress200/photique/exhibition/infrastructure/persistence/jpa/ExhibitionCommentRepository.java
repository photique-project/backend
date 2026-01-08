package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExhibitionCommentRepository extends JpaRepository<ExhibitionComment, Long> {
    @Query("""
            SELECT ec
            FROM ExhibitionComment ec
            JOIN FETCH ec.writer
            WHERE ec.exhibition.id = :exhibitionId
            AND ec.exhibition.deletedAt IS NULL
            """)
    Page<ExhibitionComment> findByExhibitionId(
            @Param("exhibitionId") Long exhibitionId,
            Pageable pageable
    );

    @Query("""
            SELECT ec
            FROM ExhibitionComment ec
            JOIN FETCH ec.writer
            WHERE ec.id = :id
            AND ec.deletedAt IS NULL
            """)
    Optional<ExhibitionComment> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
