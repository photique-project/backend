package com.benchpress200.photique.exhibition.application.query.port.out;

import java.util.List;
import java.util.Set;

public interface ExhibitionLikeQueryPort {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);

    Set<Long> findExhibitionIds(Long userId, List<Long> exhibitionIds);
}
