package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionSearchRepositoryCustom {
    Page<ExhibitionSearch> searchExhibitions(Target target, List<String> keywords, Pageable pageable);
}
