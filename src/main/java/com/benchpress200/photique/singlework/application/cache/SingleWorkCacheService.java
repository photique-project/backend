package com.benchpress200.photique.singlework.application.cache;

import com.benchpress200.photique.singlework.domain.dto.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkCacheService {
    Page<SingleWorkSearch> searchSingleWorks(
            SingleWorkSearchRequest singleWorkSearchRequest,
            Pageable pageable
    );
}
