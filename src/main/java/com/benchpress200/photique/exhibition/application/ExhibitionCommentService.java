package com.benchpress200.photique.exhibition.application;

import com.benchpress200.photique.exhibition.domain.dto.ExhibitionCommentCreateRequest;

public interface ExhibitionCommentService {
    void createExhibitionComment(ExhibitionCommentCreateRequest exhibitionCommentCreateRequest);
}
