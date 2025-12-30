package com.benchpress200.photique.singlework.application.command.port.in;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkUpdateCommand;

public interface UpdateSingleWorkDetailsUseCase {
    void updateSingleWorkDetails(SingleWorkUpdateCommand command);
}
