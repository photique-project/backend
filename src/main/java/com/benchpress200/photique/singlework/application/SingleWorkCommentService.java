package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.presentation.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.presentation.dto.SingleWorkCommentDeleteRequest;
import com.benchpress200.photique.singlework.presentation.dto.SingleWorkCommentDetailResponse;
import com.benchpress200.photique.singlework.presentation.dto.SingleWorkCommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCommentService {
    void addSingleWorkComment(SingleWorkCommentCreateRequest singleWorkCommentCreateRequest);

    void updateSingleWorkComment(SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest);

    void deleteSingleWorkComment(SingleWorkCommentDeleteRequest singleWorkCommentDeleteRequest);

    Page<SingleWorkCommentDetailResponse> getSingleWorkComments(Long singleWorkId, Pageable pageable);

}
