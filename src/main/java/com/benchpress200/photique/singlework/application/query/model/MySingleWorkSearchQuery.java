package com.benchpress200.photique.singlework.application.query.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class MySingleWorkSearchQuery {
    private String keyword;
    private Pageable pageable;
}
