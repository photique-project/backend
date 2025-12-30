package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.SearchSingleWorksQuery;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;

public interface SearchSingleWorkUseCase {
    SingleWorkSearchResult searchSingleWork(SearchSingleWorksQuery query);
}
