package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionService {
    void createNewExhibition(ExhibitionCreateRequest exhibitionCreateRequest);

    ExhibitionDetailResponse getExhibitionDetail(Long exhibitionId);

    Page<ExhibitionSearchResponse> searchExhibitions(ExhibitionSearchRequest exhibitionSearchRequest,
                                                     Pageable pageable);
}
