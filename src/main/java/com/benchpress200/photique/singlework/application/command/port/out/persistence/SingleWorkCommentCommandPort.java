package com.benchpress200.photique.singlework.application.command.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;

public interface SingleWorkCommentCommandPort {
    SingleWorkComment save(SingleWorkComment singleWorkComment);

    void delete(SingleWorkComment singleWorkComment);
}
