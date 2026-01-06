package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.application.query.result.SearchedExhibition;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class MyExhibitionSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedExhibition> exhibitions;

    public static MyExhibitionSearchResult of(
            Page<Exhibition> exhibitionPage,
            Ids likedExhibitionIds,
            Ids bookmarkedExhibitionIds
    ) {
        List<SearchedExhibition> exhibitions = exhibitionPage.stream()
                .map(exhibition -> {
                    Long exhibitionId = exhibition.getId();
                    boolean isLiked = likedExhibitionIds.contains(exhibitionId);
                    boolean isBookmarked = bookmarkedExhibitionIds.contains(exhibitionId);

                    return SearchedExhibition.of(
                            exhibition,
                            isLiked,
                            isBookmarked
                    );
                })
                .toList();

        return MyExhibitionSearchResult.builder()
                .page(exhibitionPage.getNumber())
                .size(exhibitionPage.getSize())
                .totalElements(exhibitions.size())
                .totalPages(exhibitionPage.getTotalPages())
                .isFirst(exhibitionPage.isFirst())
                .isLast(exhibitionPage.isLast())
                .hasNext(exhibitionPage.hasNext())
                .hasPrevious(exhibitionPage.hasPrevious())
                .exhibitions(exhibitions)
                .build();
    }
}
