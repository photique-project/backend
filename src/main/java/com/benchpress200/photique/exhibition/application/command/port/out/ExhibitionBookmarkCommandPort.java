package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;

public interface ExhibitionBookmarkCommandPort {
    ExhibitionBookmark save(ExhibitionBookmark exhibitionBookmark);
}
