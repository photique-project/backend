package com.benchpress200.photique.singlework.infrastructure.persistence.jpa;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SingleWorkSearchRepository extends ElasticsearchRepository<SingleWorkSearch, Long>,
        SingleWorkSearchRepositoryCustom {
    Page<SingleWorkSearch> findByWriterId(Long writerId, Pageable pageable);
}
