package com.benchpress200.photique.exhibition.application.command.port.out;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;

public interface ExhibitionWorkCommandPort {
    ExhibitionWork save(ExhibitionWork work);
}
