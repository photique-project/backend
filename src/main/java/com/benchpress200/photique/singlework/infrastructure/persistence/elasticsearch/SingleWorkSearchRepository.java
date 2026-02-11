package com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SingleWorkSearchRepository extends ElasticsearchRepository<SingleWorkSearch, Long>,
        SingleWorkSearchRepositoryCustom {
}
