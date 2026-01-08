package com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionSearchRepositoryCustom {
    Page<ExhibitionSearch> searchExhibition(
            Target target,
            String keyword,
            Pageable pageable
    );
}
