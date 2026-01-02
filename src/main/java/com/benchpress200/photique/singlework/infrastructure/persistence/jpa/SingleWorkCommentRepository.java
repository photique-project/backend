package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
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
            WHERE swc.singleWork.id = :singleWorkId
            """)
    Page<SingleWorkComment> findBySingleWorkIdWithWriter(@Param("singleWorkId") Long singleWorkId, Pageable pageable);
}
