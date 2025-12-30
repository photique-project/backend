package com.benchpress200.photique.exhibition.application.query.port.out;

import com.benchpress200.photique.user.domain.entity.User;

public interface ExhibitionQueryPort {
    Long countByWriter(User writer);
}
