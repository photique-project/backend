package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class MySingleWorkSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedSingleWork> singleWorks;

    public static MySingleWorkSearchResult of(
            List<SearchedSingleWork> searchedSingleWorks,
            Page<SingleWork> singleWorkpage
    ) {
        return MySingleWorkSearchResult.builder()
                .page(singleWorkpage.getNumber())
                .size(singleWorkpage.getSize())
                .totalElements(singleWorkpage.getTotalElements())
                .isFirst(singleWorkpage.isFirst())
                .isLast(singleWorkpage.isLast())
                .hasNext(singleWorkpage.hasNext())
                .hasPrevious(singleWorkpage.hasPrevious())
                .singleWorks(searchedSingleWorks)
                .build();
    }
}
