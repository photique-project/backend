package com.benchpress200.photique.user.application.query.result;

import com.benchpress200.photique.user.application.query.support.SearchedUsers;
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
        return FolloweeSearchResult.builder()
                .page(followeePage.getNumber())
                .size(followeePage.getSize())
                .totalElements(followeePage.getTotalElements())
                .totalPages(followeePage.getTotalPages())
                .isFirst(followeePage.isFirst())
                .isLast(followeePage.isLast())
                .hasNext(followeePage.hasNext())
                .hasPrevious(followeePage.hasPrevious())
                .followees(followees.values())
                .build();
    }
}
