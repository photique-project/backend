package com.benchpress200.photique.exhibition.application.query.port.in;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionSearchResult;

public interface SearchExhibitionUseCase {
    ExhibitionSearchResult searchExhibition(ExhibitionSearchQuery query);
}
