package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class SingleWorkSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedSingleWork> singleWorks;

    public static SingleWorkSearchResult of(
            Page<SingleWorkSearch> singleWorkpage,
            Ids likedExhibitionIds
    ) {
        List<SearchedSingleWork> singleWorks = singleWorkpage.stream()
                .map(singleWorkSearch -> {
                    Long singleWorkId = singleWorkSearch.getId();
                    boolean isLiked = likedExhibitionIds.contains(singleWorkId);

                    return SearchedSingleWork.of(singleWorkSearch, isLiked);
                })
                .toList();

        return SingleWorkSearchResult.builder()
                .page(singleWorkpage.getNumber())
                .size(singleWorkpage.getSize())
                .totalElements(singleWorkpage.getTotalElements())
                .isFirst(singleWorkpage.isFirst())
                .isLast(singleWorkpage.isLast())
                .hasNext(singleWorkpage.hasNext())
                .hasPrevious(singleWorkpage.hasPrevious())
                .singleWorks(singleWorks)
                .build();
    }
}
