package com.benchpress200.photique.user.domain.repository;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchRepository extends
        ElasticsearchRepository<UserSearch, Long>,
        UserSearchRepositoryCustom {
}
