package com.benchpress200.photique.singlework.infrastructure.persistence.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SingleWorkCommentPersistenceAdapter implements
        SingleWorkCommentCommandPort {
    private final SingleWorkCommentRepository singleWorkCommentRepository;

    @Override
    public SingleWorkComment save(SingleWorkComment singleWorkComment) {
        return singleWorkCommentRepository.save(singleWorkComment);
    }
}
