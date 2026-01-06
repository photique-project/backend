package com.benchpress200.photique.exhibition.application.query.port.in;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionCommentsQuery;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionCommentsResult;

public interface GetExhibitionCommentsUseCase {
    ExhibitionCommentsResult getExhibitionComments(ExhibitionCommentsQuery query);
}
