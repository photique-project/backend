package com.benchpress200.photique.singlework.api.query.response;

import com.benchpress200.photique.singlework.application.query.result.SearchedSingleWork;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkSearchResponse {
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
    private List<SearchedSingleWork> singleWorks;

    public static SingleWorkSearchResponse from(SingleWorkSearchResult searchSingleWorkResult) {
        return SingleWorkSearchResponse.builder()
                .page(searchSingleWorkResult.getPage())
                .size(searchSingleWorkResult.getSize())
                .totalElements(searchSingleWorkResult.getTotalElements())
                .isFirst(searchSingleWorkResult.isFirst())
                .isLast(searchSingleWorkResult.isLast())
                .hasNext(searchSingleWorkResult.isHasNext())
                .hasPrevious(searchSingleWorkResult.isHasPrevious())
                .singleWorks(searchSingleWorkResult.getSingleWorks())
                .build();
    }
}
