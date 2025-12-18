package com.benchpress200.photique.user.presentation.response;

import com.benchpress200.photique.user.application.result.SearchedUser;
import com.benchpress200.photique.user.application.result.UserSearchResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponse {
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

    public static UserSearchResponse from(UserSearchResult userSearchResult) {
        int page = userSearchResult.getPage();
        int size = userSearchResult.getSize();
        long totalElements = userSearchResult.getTotalElements();
        int totalPages = userSearchResult.getTotalPages();
        boolean isFirst = userSearchResult.isFirst();
        boolean isLast = userSearchResult.isLast();
        boolean hasNext = userSearchResult.isHasNext();
        boolean hasPrevious = userSearchResult.isHasPrevious();
        List<SearchedUser> users = userSearchResult.getUsers();

        return UserSearchResponse.builder()
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
