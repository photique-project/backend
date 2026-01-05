package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;

public interface ExhibitionLikeCommandPort {
    ExhibitionLike save(ExhibitionLike exhibitionLike);

    void delete(ExhibitionLike exhibitionLike);
}
