package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkRepositoryCustom {
    Page<SingleWork> searchSingleWorks(
            Target target,
            List<String> keywords,
            List<Category> categories,
            Pageable pageable
    );
}
