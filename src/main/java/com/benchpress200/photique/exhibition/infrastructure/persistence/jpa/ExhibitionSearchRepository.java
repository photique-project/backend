package com.benchpress200.photique.exhibition.infrastructure.persistence.jpa;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ExhibitionSearchRepository extends ElasticsearchRepository<ExhibitionSearch, Long>,
        ExhibitionSearchRepositoryCustom {
    Page<ExhibitionSearch> findByWriterId(Long writerId, Pageable pageable);

    List<ExhibitionSearch> findAllByWriterId(long writerId);
}
