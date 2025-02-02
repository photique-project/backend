package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDeleteRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentUpdateRequest;

public interface SingleWorkCommentService {
    void createSingleWorkComment(SingleWorkCommentCreateRequest singleWorkCommentCreateRequest);

    void updateSingleWorkComment(SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest);

    void deleteSingleWorkComment(SingleWorkCommentDeleteRequest singleWorkCommentDeleteRequest);
}
