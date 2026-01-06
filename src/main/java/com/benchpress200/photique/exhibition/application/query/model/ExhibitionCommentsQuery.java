package com.benchpress200.photique.exhibition.application.query.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class ExhibitionCommentsQuery {
    private Long exhibitionId;
    private Pageable pageable;
}
