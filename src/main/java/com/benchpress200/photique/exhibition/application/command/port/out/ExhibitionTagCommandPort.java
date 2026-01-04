package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;

public interface ExhibitionTagCommandPort {
    ExhibitionTag save(ExhibitionTag exhibitionTag);
}
