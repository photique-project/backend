package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.NewSingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;

public interface SingleWorkService {
    void createNewSingleWork(NewSingleWorkRequest newSingleWorkRequest);

    SingleWorkDetailResponse getSingleWorkDetail(Long singleworkId);
}
