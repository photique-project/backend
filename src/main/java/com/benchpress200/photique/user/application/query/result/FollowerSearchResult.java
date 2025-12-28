package com.benchpress200.photique.user.application.query.result;

import com.benchpress200.photique.user.application.query.support.SearchedUsers;
import com.benchpress200.photique.user.domain.entity.User;
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
        return FollowerSearchResult.builder()
                .page(followerPage.getNumber())
                .size(followerPage.getSize())
                .totalElements(followerPage.getTotalElements())
                .totalPages(followerPage.getTotalPages())
                .isFirst(followerPage.isFirst())
                .isLast(followerPage.isLast())
                .hasNext(followerPage.hasNext())
                .hasPrevious(followerPage.hasPrevious())
                .followers(followers.values())
                .build();
    }
}
