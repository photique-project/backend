package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCommentQueryPort {
    Page<SingleWorkComment> findBySingleWorkIdWithWriter(Long singleWorkId, Pageable pageable);
}
