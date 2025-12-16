package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.result.SearchedUser;
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

    public static FolloweeSearchResponse from(final FolloweeSearchResult followeeSearchResult) {
        int page = followeeSearchResult.getPage();
        int size = followeeSearchResult.getSize();
        long totalElements = followeeSearchResult.getTotalElements();
        int totalPages = followeeSearchResult.getTotalPages();
        boolean isFirst = followeeSearchResult.isFirst();
        boolean isLast = followeeSearchResult.isLast();
        boolean hasNext = followeeSearchResult.isHasNext();
        boolean hasPrevious = followeeSearchResult.isHasPrevious();
        List<SearchedUser> users = followeeSearchResult.getFollowees();

        return FolloweeSearchResponse.builder()
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
