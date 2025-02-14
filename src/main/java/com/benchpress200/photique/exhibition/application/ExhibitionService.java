package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionDetailResponse;

public interface ExhibitionService {
    void createNewExhibition(ExhibitionCreateRequest exhibitionCreateRequest);

    ExhibitionDetailResponse getExhibitionDetail(Long exhibitionId);
}
