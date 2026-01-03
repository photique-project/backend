package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.LikedSingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.LikedSingleWorkSearchResult;

public interface SearchLikedSingleWorkUseCase {
    LikedSingleWorkSearchResult searchLikedSingleWork(LikedSingleWorkSearchQuery query);
}
