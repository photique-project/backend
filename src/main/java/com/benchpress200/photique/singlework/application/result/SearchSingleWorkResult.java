package com.benchpress200.photique.singlework.application.result;

import com.benchpress200.photique.singlework.application.vo.SearchedSingleWorks;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class SearchSingleWorkResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedSingleWork> singleWorks;

    public static SearchSingleWorkResult of(
            SearchedSingleWorks searchedSingleWorks,
            Page<SingleWorkSearch> singleWorkpage
    ) {
        return SearchSingleWorkResult.builder()
                .page(singleWorkpage.getNumber())
                .size(singleWorkpage.getSize())
                .totalElements(singleWorkpage.getTotalElements())
                .isFirst(singleWorkpage.isFirst())
                .isLast(singleWorkpage.isLast())
                .hasNext(singleWorkpage.hasNext())
                .hasPrevious(singleWorkpage.hasPrevious())
                .singleWorks(searchedSingleWorks.values())
                .build();
    }
}
