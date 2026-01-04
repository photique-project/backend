package com.benchpress200.photique.exhibition.application.command.port.in;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCreateCommand;

public interface OpenExhibitionUseCase {
    void openExhibition(ExhibitionCreateCommand command);
}
