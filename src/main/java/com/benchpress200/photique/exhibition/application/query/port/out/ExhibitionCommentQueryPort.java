package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCommentQueryPort {
    Page<ExhibitionComment> findByExhibitionId(
            Long exhibitionId,
            Pageable pageable
    );

    Optional<ExhibitionComment> findByIdAndDeletedAtIsNull(Long id);
}
