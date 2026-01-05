package com.benchpress200.photique.exhibition.api.query.response;

import com.benchpress200.photique.exhibition.application.query.result.ExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.result.SearchedExhibition;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionSearchResponse {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    @JsonProperty("isFirst")
    @Getter(AccessLevel.NONE)
    private boolean isFirst;
    @JsonProperty("isLast")
    @Getter(AccessLevel.NONE)
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedExhibition> exhibitions;

    public static ExhibitionSearchResponse from(ExhibitionSearchResult result) {
        return ExhibitionSearchResponse.builder()
                .page(result.getPage())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .isFirst(result.isFirst())
                .isLast(result.isLast())
                .hasNext(result.isHasNext())
                .hasPrevious(result.isHasPrevious())
                .exhibitions(result.getExhibitions())
                .build();
    }
}
