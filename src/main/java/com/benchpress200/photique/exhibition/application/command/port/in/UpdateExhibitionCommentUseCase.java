package com.benchpress200.photique.exhibition.application.command.port.in;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;

public interface UpdateExhibitionCommentUseCase {
    void updateExhibitionComment(ExhibitionCommentUpdateCommand command);
}
