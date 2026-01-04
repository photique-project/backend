package com.benchpress200.photique.exhibition.application.query.port.in;

import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;

public interface GetExhibitionDetailsUseCase {
    ExhibitionDetailsResult getExhibitionDetails(Long exhibitionId);
}
