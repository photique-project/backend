package com.benchpress200.photique.common.application.support;

import java.util.Set;

public class Ids {
    private final Set<Long> ids;

    private Ids(Set<Long> ids) {
        this.ids = ids;
    }

    public static Ids from(Set<Long> ids) {
        return new Ids(ids);
    }

    public boolean contains(Long id) {
        return ids.contains(id);
    }

    public static Ids empty() {
        return new Ids(Set.of());
    }
}
