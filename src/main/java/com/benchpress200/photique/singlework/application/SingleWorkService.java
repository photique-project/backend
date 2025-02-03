package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkService {
    void createNewSingleWork(SingleWorkCreateRequest singleWorkCreateRequest);

    SingleWorkDetailResponse getSingleWorkDetail(Long singleworkId);

    Page<SingleWorkSearchResponse> searchSingleWorks(SingleWorkSearchRequest singleWorkSearchRequest,
                                                     Pageable pageable);

    void updateSingleWorkDetail(SingleWorkUpdateRequest singleWorkUpdateRequest);
}
