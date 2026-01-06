package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCommentQueryPort {
    Page<ExhibitionComment> findByExhibitionIdWithWriter(
            Long exhibitionId,
            Pageable pageable
    );
}
