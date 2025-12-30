package com.benchpress200.photique.singlework.application.query.port.out.persistence;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.enumeration.Category;
import com.benchpress200.photique.singlework.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SingleWorkQueryPort {
    Optional<SingleWork> findByIdWithWriter(Long id);

    Optional<SingleWork> findActiveByIdWithWriter(Long id);

    Long countByWriter(User writer);

    Page<SingleWorkSearch> search(
            Target target,
            String keyword,
            List<Category> categories,
            Pageable pageable
    );

    Optional<SingleWork> findActiveById(Long id);
}
