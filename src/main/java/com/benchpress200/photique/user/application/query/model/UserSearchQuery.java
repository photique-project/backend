package com.benchpress200.photique.user.application.query.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class UserSearchQuery {
    private String keyword;
    private Pageable pageable;
}
