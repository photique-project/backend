package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.vo.SearchedUsers;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class FollowerSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedUser> followers;

    public static FollowerSearchResult of(
            SearchedUsers followers,
            Page<User> followerPage
    ) {
        int page = followerPage.getNumber();
        int size = followerPage.getSize();
        long totalElements = followerPage.getTotalElements();
        int totalPages = followerPage.getTotalPages();
        boolean isFirst = followerPage.isFirst();
        boolean isLast = followerPage.isLast();
        boolean hasNext = followerPage.hasNext();
        boolean hasPrevious = followerPage.hasPrevious();

        return FollowerSearchResult.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .followers(followers.values())
                .build();
    }
}
