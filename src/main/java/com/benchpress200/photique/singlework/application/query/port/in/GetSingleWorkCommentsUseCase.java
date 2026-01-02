package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkCommentsQuery;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkCommentsResult;

public interface GetSingleWorkCommentsUseCase {
    SingleWorkCommentsResult getSingleWorkComments(SingleWorkCommentsQuery query);
}
