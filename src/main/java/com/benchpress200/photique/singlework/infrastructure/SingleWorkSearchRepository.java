package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SingleWorkSearchRepository extends ElasticsearchRepository<SingleWorkSearch, Long> {
}
