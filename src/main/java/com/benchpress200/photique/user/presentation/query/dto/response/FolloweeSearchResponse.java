package com.benchpress200.photique.user.presentation.query.dto.response;

import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.query.result.SearchedUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FolloweeSearchResponse {
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
    private List<SearchedUser> users;

    public static FolloweeSearchResponse from(FolloweeSearchResult followeeSearchResult) {
        return FolloweeSearchResponse.builder()
                .page(followeeSearchResult.getPage())
                .size(followeeSearchResult.getSize())
                .totalElements(followeeSearchResult.getTotalElements())
                .totalPages(followeeSearchResult.getTotalPages())
                .isFirst(followeeSearchResult.isFirst())
                .isLast(followeeSearchResult.isLast())
                .hasNext(followeeSearchResult.isHasNext())
                .hasPrevious(followeeSearchResult.isHasPrevious())
                .users(followeeSearchResult.getFollowees())
                .build();
    }
}
