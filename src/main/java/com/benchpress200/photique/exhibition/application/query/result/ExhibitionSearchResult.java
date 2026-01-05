package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class ExhibitionSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedExhibition> exhibitions;

    public static ExhibitionSearchResult of(
            Page<ExhibitionSearch> exhibitionSearchPage,
            Ids likedExhibitionIds,
            Ids bookmarkedExhibitionIds
    ) {
        List<SearchedExhibition> exhibitions = exhibitionSearchPage.stream()
                .map(exhibitionSearch -> {
                    Long exhibitionId = exhibitionSearch.getId();
                    boolean isLiked = likedExhibitionIds.contains(exhibitionId);
                    boolean isBookmarked = bookmarkedExhibitionIds.contains(exhibitionId);

                    return SearchedExhibition.of(exhibitionSearch, isLiked, isBookmarked);
                })
                .toList();

        return ExhibitionSearchResult.builder()
                .page(exhibitionSearchPage.getNumber())
                .size(exhibitionSearchPage.getSize())
                .totalElements(exhibitionSearchPage.getTotalElements())
                .isFirst(exhibitionSearchPage.isFirst())
                .isLast(exhibitionSearchPage.isLast())
                .hasNext(exhibitionSearchPage.hasNext())
                .hasPrevious(exhibitionSearchPage.hasPrevious())
                .exhibitions(exhibitions)
                .build();
    }
}
