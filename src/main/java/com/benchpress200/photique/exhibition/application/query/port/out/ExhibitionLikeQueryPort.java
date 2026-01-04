package com.benchpress200.photique.exhibition.application.query.port.out;

public interface ExhibitionLikeQueryPort {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);
}
