package com.benchpress200.photique.user.application.query;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class FollowerSearchQuery {
    private final Long userId;
    private final String keyword;
    private final Pageable pageable;
}
