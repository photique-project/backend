package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkCommentRepository extends JpaRepository<SingleWorkComment, Long> {
    Page<SingleWorkComment> findBySingleWorkId(Long singleWorkId, Pageable pageable);

    void deleteBySingleWorkId(Long singleWorkId);
}
