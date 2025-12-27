package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.application.vo.SearchedUsers;
import com.benchpress200.photique.user.domain.entity.User;
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
            SearchedUsers searchedUsers,
            Page<User> userPage
    ) {
        return UserSearchResult.builder()
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .isFirst(userPage.isFirst())
                .isLast(userPage.isLast())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .users(searchedUsers.values())
                .build();
    }
}
