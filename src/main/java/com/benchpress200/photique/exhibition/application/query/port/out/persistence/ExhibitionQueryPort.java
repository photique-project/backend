package com.benchpress200.photique.exhibition.application.query.port.out.persistence;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionQueryPort {
    Long countByWriter(User writer);

    Optional<Exhibition> findByIdAndDeletedAtIsNull(Long id);

    Page<ExhibitionSearch> searchExhibition(
            Target target,
            String keyword,
            Pageable pageable
    );

    Page<Exhibition> searchMyExhibitionByDeletedAtIsNull(
            Long userId,
            String keyword,
            Pageable pageable
    );
}
