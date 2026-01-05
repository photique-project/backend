package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchedExhibition {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private String cardColor;
    private Long likeCount;
    private Long viewCount;
    @JsonProperty("isLiked")
    @Getter(AccessLevel.NONE)
    private boolean isLiked;
    @JsonProperty("isBookmarked")
    @Getter(AccessLevel.NONE)
    private boolean isBookmarked;

    public static SearchedExhibition of(
            ExhibitionSearch exhibitionSearch,
            boolean isLiked,
            boolean isBookmarked
    ) {
        Writer writer = Writer.of(
                exhibitionSearch.getId(),
                exhibitionSearch.getWriterNickname(),
                exhibitionSearch.getWriterProfileImage()
        );

        return SearchedExhibition.builder()
                .id(exhibitionSearch.getId())
                .writer(writer)
                .title(exhibitionSearch.getTitle())
                .description(exhibitionSearch.getDescription())
                .cardColor(exhibitionSearch.getCardColor())
                .likeCount(exhibitionSearch.getLikeCount())
                .viewCount(exhibitionSearch.getViewCount())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }

    public static SearchedExhibition of(
            Exhibition exhibition,
            boolean isLiked,
            boolean isBookmarked
    ) {
        Writer writer = Writer.from(exhibition.getWriter());

        return SearchedExhibition.builder()
                .id(exhibition.getId())
                .writer(writer)
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .cardColor(exhibition.getCardColor())
                .likeCount(exhibition.getLikeCount())
                .viewCount(exhibition.getViewCount())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }

    public static SearchedExhibition of(
            ExhibitionLike exhibitionLike,
            boolean isBookmarked
    ) {
        Exhibition exhibition = exhibitionLike.getExhibition();
        Writer writer = Writer.from(exhibition.getWriter());

        return SearchedExhibition.builder()
                .id(exhibition.getId())
                .writer(writer)
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .cardColor(exhibition.getCardColor())
                .likeCount(exhibition.getLikeCount())
                .viewCount(exhibition.getViewCount())
                .isLiked(true)
                .isBookmarked(isBookmarked)
                .build();
    }

    public static SearchedExhibition of(
            ExhibitionBookmark exhibitionBookmark,
            boolean isLiked
    ) {
        Exhibition exhibition = exhibitionBookmark.getExhibition();
        Writer writer = Writer.from(exhibition.getWriter());

        return SearchedExhibition.builder()
                .id(exhibition.getId())
                .writer(writer)
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .cardColor(exhibition.getCardColor())
                .likeCount(exhibition.getLikeCount())
                .viewCount(exhibition.getViewCount())
                .isLiked(isLiked)
                .isBookmarked(true)
                .build();
    }
}
