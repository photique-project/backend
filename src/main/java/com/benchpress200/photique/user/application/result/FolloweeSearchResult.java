package com.benchpress200.photique.user.application.result;

import com.benchpress200.photique.user.application.vo.SearchedUsers;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class FolloweeSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedUser> followees;

    public static FolloweeSearchResult of(
            SearchedUsers followees,
            Page<User> followeePage
    ) {
        int page = followeePage.getNumber();
        int size = followeePage.getSize();
        long totalElements = followeePage.getTotalElements();
        int totalPages = followeePage.getTotalPages();
        boolean isFirst = followeePage.isFirst();
        boolean isLast = followeePage.isLast();
        boolean hasNext = followeePage.hasNext();
        boolean hasPrevious = followeePage.hasPrevious();

        return FolloweeSearchResult.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isFirst(isFirst)
                .isLast(isLast)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .followees(followees.values())
                .build();
    }
}
