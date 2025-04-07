package com.benchpress200.photique.user.infrastructure;

import com.benchpress200.photique.user.domain.entity.UserSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserSearchRepositoryCustom {
    Page<UserSearch> search(String keyword, Pageable pageable);
}
