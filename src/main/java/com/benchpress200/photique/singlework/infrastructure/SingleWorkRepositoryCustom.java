package com.benchpress200.photique.singlework.infrastructure;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkRepositoryCustom {
    Page<SingleWork> searchWorks(
            String q,
            String target,
            String sort,
            List<String> categories,
            Pageable pageable
    );
}
