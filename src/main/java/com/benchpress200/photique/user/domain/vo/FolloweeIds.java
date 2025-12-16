package com.benchpress200.photique.user.domain.vo;

import java.util.Set;

public class FolloweeIds {
    private final Set<Long> followeeIds;

    private FolloweeIds(final Set<Long> followeeIds) {
        this.followeeIds = followeeIds;
    }

    public static FolloweeIds from(final Set<Long> followeeIds) {
        return new FolloweeIds(followeeIds);
    }

    public boolean contains(final Long followerId) {
        return followeeIds.contains(followerId);
    }
}
