package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleWorkCommentRepository extends JpaRepository<SingleWorkComment, Long> {
}
