package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class LikedSingleWorkSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedSingleWork> singleWorks;

    public static LikedSingleWorkSearchResult from(
            Page<SingleWorkLike> likedSingleWorkPage
    ) {
        List<SearchedSingleWork> searchedSingleWorks = likedSingleWorkPage.getContent().stream()
                .map(SearchedSingleWork::from)
                .toList();

        return LikedSingleWorkSearchResult.builder()
                .page(likedSingleWorkPage.getNumber())
                .size(likedSingleWorkPage.getSize())
                .totalElements(likedSingleWorkPage.getTotalElements())
                .isFirst(likedSingleWorkPage.isFirst())
                .isLast(likedSingleWorkPage.isLast())
                .hasNext(likedSingleWorkPage.hasNext())
                .hasPrevious(likedSingleWorkPage.hasPrevious())
                .singleWorks(searchedSingleWorks)
                .build();
    }
}
