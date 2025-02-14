package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
}
