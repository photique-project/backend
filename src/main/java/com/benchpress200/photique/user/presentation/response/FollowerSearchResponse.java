package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.FollowerSearchResult;
import com.benchpress200.photique.user.application.result.SearchedUser;
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
        int page = followerSearchResult.getPage();
        int size = followerSearchResult.getSize();
        long totalElements = followerSearchResult.getTotalElements();
        int totalPages = followerSearchResult.getTotalPages();
        boolean isFirst = followerSearchResult.isFirst();
        boolean isLast = followerSearchResult.isLast();
        boolean hasNext = followerSearchResult.isHasNext();
        boolean hasPrevious = followerSearchResult.isHasPrevious();
        List<SearchedUser> users = followerSearchResult.getFollowers();

        return FollowerSearchResponse.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .users(users)
                .build();
    }
}
