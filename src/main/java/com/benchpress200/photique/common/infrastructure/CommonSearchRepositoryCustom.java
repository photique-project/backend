package com.benchpress200.photique.common.infrastructure;

import java.util.List;

// ES 업데이트 로직 간결화를 위한 인터페이스와 구현체
public interface CommonSearchRepositoryCustom {
    void update(Object document);

    void updateAll(List<?> documents);
}
