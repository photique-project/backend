package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.MySingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;

public interface SearchMySingleWorkUseCase {
    MySingleWorkSearchResult searchMySingleWork(MySingleWorkSearchQuery query);
}
