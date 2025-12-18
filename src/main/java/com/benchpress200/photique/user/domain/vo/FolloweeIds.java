package com.benchpress200.photique.user.domain.vo;

import java.util.Set;

public class FolloweeIds {
    private final Set<Long> followeeIds;

    private FolloweeIds(Set<Long> followeeIds) {
        this.followeeIds = followeeIds;
    }

    public static FolloweeIds from(Set<Long> followeeIds) {
        return new FolloweeIds(followeeIds);
    }

    public boolean contains(Long followerId) {
        return followeeIds.contains(followerId);
    }
}
