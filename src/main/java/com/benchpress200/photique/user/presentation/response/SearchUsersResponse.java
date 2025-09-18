package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.SearchUsersResult;
import com.benchpress200.photique.user.application.result.SearchedUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchUsersResponse {
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

    public static SearchUsersResponse from(final SearchUsersResult searchUsersResult) {
        int page = searchUsersResult.getPage();
        int size = searchUsersResult.getSize();
        long totalElements = searchUsersResult.getTotalElements();
        int totalPages = searchUsersResult.getTotalPages();
        boolean isFirst = searchUsersResult.isFirst();
        boolean isLast = searchUsersResult.isLast();
        boolean hasNext = searchUsersResult.isHasNext();
        boolean hasPrevious = searchUsersResult.isHasPrevious();
        List<SearchedUser> users = searchUsersResult.getUsers();

        return SearchUsersResponse.builder()
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
