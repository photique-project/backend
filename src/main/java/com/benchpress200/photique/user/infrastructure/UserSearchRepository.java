package com.benchpress200.photique.user.infrastructure;

import com.benchpress200.photique.common.infrastructure.CommonSearchRepositoryCustom;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchRepository extends ElasticsearchRepository<UserSearch, Long>, UserSearchRepositoryCustom,
        CommonSearchRepositoryCustom {
}
