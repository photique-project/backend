package com.benchpress200.photique.singlework.application.command.port.in;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentUpdateCommand;

public interface UpdateSingleWorkCommentUseCase {
    void updateSingleWorkComment(SingleWorkCommentUpdateCommand command);
}
