package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionCommentRepository extends JpaRepository<ExhibitionComment, Long> {
    Long countByExhibitionId(Long exhibitionId);
}
