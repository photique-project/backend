package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.MyExhibitionSearchQuery;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;

public interface SearchMyExhibitionUseCase {
    MyExhibitionSearchResult searchMyExhibition(MyExhibitionSearchQuery query);
}
