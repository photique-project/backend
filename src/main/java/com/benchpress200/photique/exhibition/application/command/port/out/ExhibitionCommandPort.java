package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;

public interface ExhibitionCommandPort {
    Exhibition save(Exhibition exhibition);

    void incrementViewCount(Long exhibitionId);

    void incrementLikeCount(Long exhibitionId);

    void decrementLikeCount(Long exhibitionId);
}
