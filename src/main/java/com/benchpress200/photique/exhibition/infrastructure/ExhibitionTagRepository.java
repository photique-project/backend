package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionTagRepository extends JpaRepository<ExhibitionTag, Long> {
}
