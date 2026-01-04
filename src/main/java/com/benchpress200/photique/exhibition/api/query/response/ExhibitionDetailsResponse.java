package com.benchpress200.photique.exhibition.api.query.response;

import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionWorkResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionWriterResult;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionDetailsResponse {
    private Long id;
    private ExhibitionWriterResult writer;
    private String title;
    private String description;
    private List<String> tags;
    private List<ExhibitionWorkResult> works;
    private Long viewCount;
    private Long likeCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @JsonProperty("isFollowing")
    private boolean isFollowing;
    @JsonProperty("isLiked")
    private boolean isLiked;
    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    public static ExhibitionDetailsResponse from(ExhibitionDetailsResult result) {
        return ExhibitionDetailsResponse.builder()
                .id(result.getId())
                .writer(result.getWriter())
                .title(result.getTitle())
                .description(result.getDescription())
                .tags(result.getTags())
                .works(result.getWorks())
                .viewCount(result.getViewCount())
                .likeCount(result.getLikeCount())
                .createdAt(result.getCreatedAt())
                .isFollowing(result.isFollowing())
                .isLiked(result.isLiked())
                .isBookmarked(result.isBookmarked())
                .build();
    }

    ;
}
