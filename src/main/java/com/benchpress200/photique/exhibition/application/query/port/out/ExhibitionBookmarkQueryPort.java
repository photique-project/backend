package com.benchpress200.photique.exhibition.application.query.port.out;

public interface ExhibitionBookmarkQueryPort {
    boolean existsByUserIdAndExhibitionId(Long userId, Long exhibitionId);
}
