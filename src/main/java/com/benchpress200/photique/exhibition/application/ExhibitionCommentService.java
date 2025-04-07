package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDeleteRequest;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentDetailResponse;
import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCommentService {
    void addExhibitionComment(ExhibitionCommentCreateRequest exhibitionCommentCreateRequest);

    Page<ExhibitionCommentDetailResponse> getExhibitionComments(Long exhibitionId, Pageable pageable);

    void updateExhibitionComment(ExhibitionCommentUpdateRequest exhibitionCommentUpdateRequest);

    void deleteExhibitionComment(ExhibitionCommentDeleteRequest exhibitionCommentDeleteRequest);
}
