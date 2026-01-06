package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
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
            Page<SingleWork> singleWorkPage,
            Ids likedSingleWorkIds
    ) {
        List<SearchedSingleWork> singleWorks = singleWorkPage.stream()
                .map(singleWork -> {
                    Long singleWorkId = singleWork.getId();
                    boolean isLiked = likedSingleWorkIds.contains(singleWorkId);

                    return SearchedSingleWork.of(singleWork, isLiked);
                })
                .toList();

        return MySingleWorkSearchResult.builder()
                .page(singleWorkPage.getNumber())
                .size(singleWorkPage.getSize())
                .totalElements(singleWorkPage.getTotalElements())
                .totalPages(singleWorkPage.getTotalPages())
                .isFirst(singleWorkPage.isFirst())
                .isLast(singleWorkPage.isLast())
                .hasNext(singleWorkPage.hasNext())
                .hasPrevious(singleWorkPage.hasPrevious())
                .singleWorks(singleWorks)
                .build();
    }
}
