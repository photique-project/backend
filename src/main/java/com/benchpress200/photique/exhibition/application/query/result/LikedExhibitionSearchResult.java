package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.common.application.support.Ids;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class LikedExhibitionSearchResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<SearchedExhibition> exhibitions;

    public static LikedExhibitionSearchResult of(
            Page<ExhibitionLike> exhibitionLikePage,
            Ids bookmarkedExhibitionIds
    ) {
        List<SearchedExhibition> searchedExhibitions = exhibitionLikePage.getContent().stream()
                .map(exhibitionLike -> {
                    Exhibition exhibition = exhibitionLike.getExhibition();
                    Long exhibitionId = exhibition.getId();

                    boolean isBookmarked = bookmarkedExhibitionIds.contains(exhibitionId);

                    return SearchedExhibition.of(
                            exhibition,
                            true,
                            isBookmarked
                    );
                })
                .toList();

        return LikedExhibitionSearchResult.builder()
                .page(exhibitionLikePage.getNumber())
                .size(exhibitionLikePage.getSize())
                .totalElements(exhibitionLikePage.getTotalElements())
                .totalPages(exhibitionLikePage.getTotalPages())
                .isFirst(exhibitionLikePage.isFirst())
                .isLast(exhibitionLikePage.isLast())
                .hasNext(exhibitionLikePage.hasNext())
                .hasPrevious(exhibitionLikePage.hasPrevious())
                .exhibitions(searchedExhibitions)
                .build();
    }
}
