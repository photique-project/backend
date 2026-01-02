package com.benchpress200.photique.singlework.application.query.service;

import com.benchpress200.photique.singlework.application.query.model.SingleWorkCommentsQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkCommentsUseCase;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkCommentsResult;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SingleWorkCommentQueryService implements
        GetSingleWorkCommentsUseCase {
    private final SingleWorkCommentQueryPort singleWorkCommentQueryPort;

    @Override
    public SingleWorkCommentsResult getSingleWorkComments(SingleWorkCommentsQuery query) {
        Long singleWorkId = query.getSingleWorkId();
        Pageable pageable = query.getPageable();

        Page<SingleWorkComment> singleWorkCommentPage = singleWorkCommentQueryPort.findBySingleWorkIdWithWriter(
                singleWorkId, pageable);

        return SingleWorkCommentsResult.from(singleWorkCommentPage);
    }
}
