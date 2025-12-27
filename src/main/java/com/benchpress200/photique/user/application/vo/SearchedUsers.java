package com.benchpress200.photique.user.application.vo;

import com.benchpress200.photique.user.application.result.SearchedUser;
import com.benchpress200.photique.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class SearchedUsers {
    private final List<SearchedUser> searchedUsers;

    private SearchedUsers(List<SearchedUser> searchedUsers) {
        this.searchedUsers = searchedUsers;
    }

    public static SearchedUsers of(
            Page<User> userPage,
            FolloweeIds followeeIds
    ) {
        List<SearchedUser> searchedUsers = userPage.stream()
                .map(user -> {
                    Long userId = user.getId();
                    boolean isFollowing = followeeIds.contains(userId);

                    return SearchedUser.of(user, isFollowing);
                })
                .toList();

        return new SearchedUsers(searchedUsers);
    }

    public List<SearchedUser> values() {
        return new ArrayList<>(searchedUsers);
    }
}
