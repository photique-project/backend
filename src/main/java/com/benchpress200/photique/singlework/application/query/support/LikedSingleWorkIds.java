package com.benchpress200.photique.singlework.application.query.support;

import java.util.Set;

public class LikedSingleWorkIds {
    private final Set<Long> ids;

    private LikedSingleWorkIds(Set<Long> ids) {
        this.ids = ids;
    }

    public static LikedSingleWorkIds from(Set<Long> ids) {
        return new LikedSingleWorkIds(ids);
    }

    public boolean contains(Long id) {
        return ids.contains(id);
    }

    public static LikedSingleWorkIds empty() {
        return new LikedSingleWorkIds(Set.of());
    }
}
