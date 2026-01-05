package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionQueryPort {
    Long countByWriter(User writer);

    Optional<Exhibition> findActiveById(Long id);

    Optional<Exhibition> findActiveByIdWithWriter(Long id);

    Page<ExhibitionSearch> search(
            Target target,
            String keyword,
            Pageable pageable
    );
}
