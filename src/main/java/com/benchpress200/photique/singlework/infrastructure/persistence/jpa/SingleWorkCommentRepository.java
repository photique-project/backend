package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingleWorkCommentRepository extends JpaRepository<SingleWorkComment, Long> {

    @Query("""
            SELECT swc
            FROM SingleWorkComment swc
            JOIN FETCH swc.writer
            WHERE swc.id = :id
            AND swc.deletedAt IS NULL
            """)
    Optional<SingleWorkComment> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    @Query("""
            SELECT swc
            FROM SingleWorkComment swc
            JOIN FETCH swc.writer
            WHERE swc.singleWork.id = :singleWorkId
            AND swc.deletedAt IS NULL
            """)
    Page<SingleWorkComment> findBySingleWorkIdAndDeletedAtIsNull(
            @Param("singleWorkId") Long singleWorkId,
            Pageable pageable
    );
}
