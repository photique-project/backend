package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.tag.domain.dto.TagResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionSearchResponse {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private String cardColor;
    private Long likeCount;
    private Long viewCount;
    private List<TagResponse> tags;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
    @JsonProperty("isLiked")
    private boolean liked;
    @JsonProperty("isBookmarked")
    private boolean bookmarked;

    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage,
            String introduction
    ) {
        public static Writer of(
                final Long id,
                final String nickname,
                final String profileImage,
                final String introduction
        ) {
            return Writer.builder()
                    .id(id)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .introduction(introduction)
                    .build();
        }
    }

    public static ExhibitionSearchResponse of(
            final ExhibitionSearch exhibitionSearch,
            final boolean isLiked,
            final boolean isBookmarked
    ) {
        return ExhibitionSearchResponse.builder()
                .id(exhibitionSearch.getId())
                .writer(
                        Writer.of(
                                exhibitionSearch.getWriterId(),
                                exhibitionSearch.getWriterNickname(),
                                exhibitionSearch.getWriterProfileImage(),
                                exhibitionSearch.getWriterIntroduction()
                        )
                )
                .title(exhibitionSearch.getTitle())
                .description(exhibitionSearch.getDescription())
                .cardColor(exhibitionSearch.getCardColor())
                .likeCount(exhibitionSearch.getLikeCount())
                .viewCount(exhibitionSearch.getViewCount())
                .tags(exhibitionSearch.getTags().stream()
                        .map(TagResponse::from).toList())

                .createdAt(exhibitionSearch.getCreatedAt().toLocalDate())
                .liked(isLiked)
                .bookmarked(isBookmarked)
                .build();

    }
}
