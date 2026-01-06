package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class BookmarkedExhibitionSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedExhibition> exhibitions;

    public static BookmarkedExhibitionSearchResult of(
            Page<ExhibitionBookmark> exhibitionBookmarkPage,
            Ids likedExhibitionIds
    ) {
        List<SearchedExhibition> searchedExhibitions = exhibitionBookmarkPage.stream()
                .map(exhibitionBookmark -> {
                    Exhibition exhibition = exhibitionBookmark.getExhibition();
                    Long exhibitionId = exhibition.getId();

                    boolean isLiked = likedExhibitionIds.contains(exhibitionId);

                    return SearchedExhibition.of(
                            exhibition,
                            isLiked,
                            true
                    );
                })
                .toList();

        return BookmarkedExhibitionSearchResult.builder()
                .page(exhibitionBookmarkPage.getNumber())
                .size(exhibitionBookmarkPage.getSize())
                .totalElements(exhibitionBookmarkPage.getTotalElements())
                .totalPages(exhibitionBookmarkPage.getTotalPages())
                .isFirst(exhibitionBookmarkPage.isFirst())
                .isLast(exhibitionBookmarkPage.isLast())
                .hasNext(exhibitionBookmarkPage.hasNext())
                .hasPrevious(exhibitionBookmarkPage.hasPrevious())
                .exhibitions(searchedExhibitions)
                .build();
    }
}
