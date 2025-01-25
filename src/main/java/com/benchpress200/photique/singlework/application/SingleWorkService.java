package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.NewSingleWorkRequest;

public interface SingleWorkService {
    void createNewSingleWork(NewSingleWorkRequest newSingleWorkRequest);
}
