package com.benchpress200.photique.singlework.application.vo;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;

public class SingleWorkIds {
    private final List<Long> ids;

    private SingleWorkIds(List<Long> ids) {
        this.ids = List.copyOf(ids);
    }

    public static SingleWorkIds from(Page<SingleWorkSearch> singleWorkPage) {
        List<Long> ids = singleWorkPage.stream()
                .map(SingleWorkSearch::getId)
                .toList();

        return new SingleWorkIds(ids);
    }

    public List<Long> values() {
        return new ArrayList<>(ids);
    }
}
