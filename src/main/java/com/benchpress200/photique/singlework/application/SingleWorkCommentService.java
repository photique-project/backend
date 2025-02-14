package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDeleteRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCommentService {
    void createSingleWorkComment(SingleWorkCommentCreateRequest singleWorkCommentCreateRequest);

    void updateSingleWorkComment(SingleWorkCommentUpdateRequest singleWorkCommentUpdateRequest);

    void deleteSingleWorkComment(SingleWorkCommentDeleteRequest singleWorkCommentDeleteRequest);

    Page<SingleWorkCommentDetailResponse> getSingleWorkComments(Long singleWorkId, Pageable pageable);

}
