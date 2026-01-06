package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;

public interface ExhibitionCommentCommandPort {
    ExhibitionComment save(ExhibitionComment exhibitionComment);

    void delete(ExhibitionComment exhibitionComment);
}
