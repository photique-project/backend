package com.benchpress200.photique.singlework.application.command.port.in;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCreateCommand;

public interface PostSingleWorkUseCase {
    void postSingleWork(SingleWorkCreateCommand command);
}
