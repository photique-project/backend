package com.benchpress200.photique.singlework.application.command.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;

public interface SingleWorkCommandPort {
    SingleWork save(SingleWork singleWork);

    void incrementViewCount(Long singleWorkId);
}
