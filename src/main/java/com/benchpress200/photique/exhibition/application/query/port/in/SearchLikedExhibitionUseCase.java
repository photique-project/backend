package com.benchpress200.photique.exhibition.application.query.port.in;

import com.benchpress200.photique.exhibition.application.query.model.LikedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.result.LikedExhibitionSearchResult;

public interface SearchLikedExhibitionUseCase {
    LikedExhibitionSearchResult searchLikedExhibition(LikedExhibitionSearchQuery query);
}
