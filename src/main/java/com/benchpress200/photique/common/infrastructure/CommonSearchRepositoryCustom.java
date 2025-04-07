package com.benchpress200.photique.common.infrastructure;

import java.util.List;

public interface CommonSearchRepositoryCustom {
    void update(Object document);

    void updateAll(List<?> documents);
}
