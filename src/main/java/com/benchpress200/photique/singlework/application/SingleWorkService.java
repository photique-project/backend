package com.benchpress200.photique.singlework.application;

import com.benchpress200.photique.singlework.domain.dto.PopularSingleWorkRequest;
import com.benchpress200.photique.singlework.domain.dto.PopularSingleWorkResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkDetailResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeDecrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkLikeIncrementRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.domain.dto.SingleWorkUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkService {
    void postNewSingleWork(SingleWorkCreateRequest singleWorkCreateRequest);

    SingleWorkDetailResponse getSingleWorkDetail(SingleWorkDetailRequest singleWorkDetailRequest);

    Page<SingleWorkSearchResponse> searchSingleWorks(
            SingleWorkSearchRequest singleWorkSearchRequest,
            Pageable pageable
    );

    void updateSingleWorkDetail(SingleWorkUpdateRequest singleWorkUpdateRequest);

    void removeSingleWork(Long singleworkId);

    void incrementLike(SingleWorkLikeIncrementRequest singleWorkLikeIncrementRequest);

    void decrementLike(SingleWorkLikeDecrementRequest singleWorkLikeDecrementRequest);

    PopularSingleWorkResponse getPopularSingleWork(PopularSingleWorkRequest popularSingleWorkRequest);
}
