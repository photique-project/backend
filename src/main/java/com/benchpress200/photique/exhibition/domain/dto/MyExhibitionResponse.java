package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.tag.domain.dto.TagResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyExhibitionResponse {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private String cardColor;
    private Long likeCount;
    private Long viewCount;
    private List<TagResponse> tags;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonProperty("isLiked")
    private boolean isLiked;
    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage,
            String introduction
    ) {
        public static Writer of(
                Long id,
                String nickname,
                String profileImage,
                String introduction
        ) {
            return Writer.builder()
                    .id(id)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .introduction(introduction)
                    .build();
        }
    }

    public static MyExhibitionResponse of(
            ExhibitionSearch exhibitionSearch,
            boolean isLiked,
            boolean isBookmarked
    ) {

        return MyExhibitionResponse.builder()
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

                .createdAt(exhibitionSearch.getCreatedAt())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();

    }
}
