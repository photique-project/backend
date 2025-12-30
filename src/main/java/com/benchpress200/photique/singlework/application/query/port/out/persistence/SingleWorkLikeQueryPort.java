package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import java.util.List;
import java.util.Set;

public interface SingleWorkLikeQueryPort {
    Long countBySingleWork(SingleWork singleWork);

    boolean existsByUserIdAndSingleWorkId(Long userId, Long singleWorkId);

    Set<Long> findSingleWorkIds(Long userId, List<Long> singleWorkIds);
}
