package com.benchpress200.photique.exhibition.domain.repository;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionTagRepository extends JpaRepository<ExhibitionTag, Long> {
    void deleteByExhibitionId(Long exhibitionId);

    void deleteByExhibition(Exhibition exhibition);

    List<ExhibitionTag> findByExhibition(Exhibition exhibition);
}
