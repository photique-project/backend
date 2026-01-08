package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCommentQueryPort {
    Optional<SingleWorkComment> findByIdAndDeletedAtIsNull(Long id);

    Page<SingleWorkComment> findBySingleWorkIdAndDeletedAtIsNull(Long singleWorkId, Pageable pageable);
}
