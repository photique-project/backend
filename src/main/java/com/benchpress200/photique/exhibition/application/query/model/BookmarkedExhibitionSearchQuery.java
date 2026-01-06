package com.benchpress200.photique.exhibition.application.query.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class BookmarkedExhibitionSearchQuery {
    private String keyword;
    private Pageable pageable;
}
