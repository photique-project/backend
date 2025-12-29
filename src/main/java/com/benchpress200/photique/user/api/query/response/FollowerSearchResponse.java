package com.benchpress200.photique.user.api.query.response;

import com.benchpress200.photique.user.application.query.result.FollowerSearchResult;
import com.benchpress200.photique.user.application.query.result.SearchedUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerSearchResponse {
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

    public static FollowerSearchResponse from(FollowerSearchResult followerSearchResult) {
        return FollowerSearchResponse.builder()
                .page(followerSearchResult.getPage())
                .size(followerSearchResult.getSize())
                .totalElements(followerSearchResult.getTotalElements())
                .totalPages(followerSearchResult.getTotalPages())
                .isFirst(followerSearchResult.isFirst())
                .isLast(followerSearchResult.isLast())
                .hasNext(followerSearchResult.isHasNext())
                .hasPrevious(followerSearchResult.isHasPrevious())
                .users(followerSearchResult.getFollowers())
                .build();
    }
}
