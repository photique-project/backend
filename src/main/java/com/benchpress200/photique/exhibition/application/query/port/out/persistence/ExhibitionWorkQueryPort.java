package com.benchpress200.photique.exhibition.application.query.port.out.persistence;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import java.util.List;
import java.util.Optional;

public interface ExhibitionWorkQueryPort {
    Optional<ExhibitionWork> findById(Long id);

    List<ExhibitionWork> findByExhibition(Exhibition exhibition);
}
