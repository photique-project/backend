package com.benchpress200.photique.user.application.vo;

import java.util.Set;

public class FolloweeIds {
    private final Set<Long> ids;

    private FolloweeIds(Set<Long> ids) {
        this.ids = ids;
    }

    public static FolloweeIds from(Set<Long> ids) {
        return new FolloweeIds(ids);
    }

    public boolean contains(Long id) {
        return ids.contains(id);
    }
}
