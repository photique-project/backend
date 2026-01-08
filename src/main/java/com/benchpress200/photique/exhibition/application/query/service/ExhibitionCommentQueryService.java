package com.benchpress200.photique.exhibition.application.query.service;

import com.benchpress200.photique.exhibition.application.query.model.ExhibitionCommentsQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionCommentsUseCase;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionCommentsResult;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionCommentQueryService implements
        GetExhibitionCommentsUseCase {
    private final ExhibitionCommentQueryPort exhibitionCommentQueryPort;

    @Override
    public ExhibitionCommentsResult getExhibitionComments(ExhibitionCommentsQuery query) {
        Long exhibitionId = query.getExhibitionId();
        Pageable pageable = query.getPageable();

        Page<ExhibitionComment> exhibitionCommentPage = exhibitionCommentQueryPort.findByExhibitionId(
                exhibitionId,
                pageable
        );

        return ExhibitionCommentsResult.from(exhibitionCommentPage);
    }
}
