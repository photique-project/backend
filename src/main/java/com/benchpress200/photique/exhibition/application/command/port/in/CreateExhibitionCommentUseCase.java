package com.benchpress200.photique.exhibition.application.command.port.in;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;

public interface CreateExhibitionCommentUseCase {
    void createExhibitionComment(ExhibitionCommentCreateCommand command);
}
