package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.common.infrastructure.CommonSearchRepositoryCustom;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SingleWorkSearchRepository extends ElasticsearchRepository<SingleWorkSearch, Long>,
        SingleWorkSearchRepositoryCustom, CommonSearchRepositoryCustom {
    Page<SingleWorkSearch> findByWriterId(Long writerId, Pageable pageable);

    List<SingleWorkSearch> findAllByWriterId(Long writerId);
}
