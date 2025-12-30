package com.benchpress200.photique.singlework.application.query.port.in;

import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;

public interface GetSingleWorkDetailsUseCase {
    SingleWorkDetailsResult getSingleWorkDetails(Long singleworkId);
}
