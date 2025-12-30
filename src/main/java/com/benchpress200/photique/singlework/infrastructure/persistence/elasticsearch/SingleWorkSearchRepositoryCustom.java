package com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkSearchRepositoryCustom {
    Page<SingleWorkSearch> search(
            Target target,
            String keyword,
            List<Category> categories,
            Pageable pageable
    );
}
