package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;

public interface SingleWorkService {
    void createNewSingleWork(SingleWorkCreateRequest singleWorkCreateRequest);

    SingleWorkDetailResponse getSingleWorkDetail(Long singleworkId);

    void getSingleWorks(SingleWorkSearchRequest singleWorkSearchRequest);
}
