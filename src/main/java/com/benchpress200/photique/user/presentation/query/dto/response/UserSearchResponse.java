package com.benchpress200.photique.user.presentation.query.dto.response;

import com.benchpress200.photique.user.application.query.result.SearchedUser;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
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
        return UserSearchResponse.builder()
                .page(userSearchResult.getPage())
                .size(userSearchResult.getSize())
                .totalElements(userSearchResult.getTotalElements())
                .totalPages(userSearchResult.getTotalPages())
                .isFirst(userSearchResult.isFirst())
                .isLast(userSearchResult.isLast())
                .hasNext(userSearchResult.isHasNext())
                .hasPrevious(userSearchResult.isHasPrevious())
                .users(userSearchResult.getUsers())
                .build();
    }
}
