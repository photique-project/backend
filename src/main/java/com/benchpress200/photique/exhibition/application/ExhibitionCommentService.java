package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCommentService {
    void createExhibitionComment(ExhibitionCommentCreateRequest exhibitionCommentCreateRequest);

    Page<ExhibitionCommentDetailResponse> getExhibitionComments(Long exhibitionId, Pageable pageable);
}
