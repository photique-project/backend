package com.benchpress200.photique.exhibition.application.cache;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCacheService {
    Page<ExhibitionSearch> searchExhibitions(ExhibitionSearchRequest exhibitionSearchRequest, Pageable pageable);
}
