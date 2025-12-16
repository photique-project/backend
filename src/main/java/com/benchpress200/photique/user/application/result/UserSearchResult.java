package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.vo.SearchedUsers;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class UserSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedUser> users;

    public static UserSearchResult of(
            final SearchedUsers searchedUsers,
            final Page<User> userPage
    ) {
        int page = userPage.getNumber();
        int size = userPage.getSize();
        long totalElements = userPage.getTotalElements();
        int totalPages = userPage.getTotalPages();
        boolean isFirst = userPage.isFirst();
        boolean isLast = userPage.isLast();
        boolean hasNext = userPage.hasNext();
        boolean hasPrevious = userPage.hasPrevious();

        return UserSearchResult.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .users(searchedUsers.values())
                .build();
    }
}
