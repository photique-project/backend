package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.Optional;

public interface ExhibitionQueryPort {
    Long countByWriter(User writer);

    Optional<Exhibition> findActiveByIdWithWriter(Long id);
}
