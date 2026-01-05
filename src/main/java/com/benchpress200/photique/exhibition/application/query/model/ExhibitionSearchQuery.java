package com.benchpress200.photique.exhibition.application.query.model;


import com.benchpress200.photique.exhibition.domain.enumeration.Target;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class ExhibitionSearchQuery {
    private Target target;
    private String keyword;
    private Pageable pageable;
}
