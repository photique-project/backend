package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.common.domain.dto.TagResponse;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionSearchResponse {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private String cardColor;
    private Long likeCount;
    private Long viewCount;
    private Integer participants;
    private List<TagResponse> tags;
    private LocalDateTime createdAt;

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

    public static ExhibitionSearchResponse from(final ExhibitionSearch exhibitionSearch) {
        return ExhibitionSearchResponse.builder()
                .id(exhibitionSearch.getId())
                .writer(
                        Writer.of(
                                exhibitionSearch.getWriterId(),
                                exhibitionSearch.getWriterNickname(),
                                exhibitionSearch.getWriterProfileImage(),
                                exhibitionSearch.getIntroduction()
                        )
                )
                .title(exhibitionSearch.getTitle())
                .description(exhibitionSearch.getDescription())
                .cardColor(exhibitionSearch.getCardColor())
                .likeCount(exhibitionSearch.getLikeCount())
                .viewCount(exhibitionSearch.getViewCount())
                .participants(exhibitionSearch.getParticipants())
                .tags(exhibitionSearch.getTags().stream()
                        .map(TagResponse::from).toList())

                .createdAt(exhibitionSearch.getCreatedAt())
                .build();

    }
}
