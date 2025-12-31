package com.benchpress200.photique.singlework.application.command.port.in;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;

public interface CreateSingleWorkCommentUseCase {
    void createSingleWorkComment(SingleWorkCommentCreateCommand command);
}
