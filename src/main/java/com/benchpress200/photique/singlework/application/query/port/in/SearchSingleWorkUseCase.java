package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;

public interface SearchSingleWorkUseCase {
    SingleWorkSearchResult searchSingleWork(SingleWorkSearchQuery query);
}
