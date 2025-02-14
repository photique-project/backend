package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionWorkRepository extends JpaRepository<ExhibitionWork, Long> {
    List<ExhibitionWork> findByExhibitionId(Long exhibitionId);
}
