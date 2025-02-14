package com.benchpress200.photique.exhibition.infrastructure;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ExhibitionSearchRepository extends ElasticsearchRepository<ExhibitionSearch, Long>,
        ExhibitionSearchRepositoryCustom {
}
