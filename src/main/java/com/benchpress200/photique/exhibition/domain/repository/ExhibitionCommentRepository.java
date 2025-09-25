package com.benchpress200.photique.exhibition.domain.repository;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionCommentRepository extends JpaRepository<ExhibitionComment, Long> {
    Page<ExhibitionComment> findByExhibition(Exhibition exhibition, Pageable pageable);

    void deleteByWriter(User user);

    void deleteByExhibition(Exhibition exhibition);

    Long countByExhibition(Exhibition exhibition);
}
