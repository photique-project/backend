package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleWorkCommentPersistenceAdapter implements
        SingleWorkCommentCommandPort,
        SingleWorkCommentQueryPort {
    private final SingleWorkCommentRepository singleWorkCommentRepository;

    @Override
    public SingleWorkComment save(SingleWorkComment singleWorkComment) {
        return singleWorkCommentRepository.save(singleWorkComment);
    }

    @Override
    public Page<SingleWorkComment> findBySingleWorkIdWithWriter(Long singleWorkId, Pageable pageable) {
        return singleWorkCommentRepository.findBySingleWorkIdWithWriter(singleWorkId, pageable);
    }
}
