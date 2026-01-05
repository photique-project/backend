package com.benchpress200.photique.exhibition.application.query.result;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.tag.domain.entity.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionDetailsResult {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private List<String> tags;
    private List<ExhibitionWorkResult> works;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private boolean isFollowing;
    private boolean isLiked;
    private boolean isBookmarked;

    public static ExhibitionDetailsResult of(
            Exhibition exhibition,
            List<Tag> tags,
            List<ExhibitionWork> exhibitionWorks,
            boolean isFollowing,
            boolean isLiked,
            boolean isBookmarked
    ) {
        Writer writer = Writer.from(exhibition.getWriter());

        List<String> tagNames = tags.stream()
                .map(Tag::getName)
                .toList();

        List<ExhibitionWorkResult> works = exhibitionWorks.stream()
                .map(ExhibitionWorkResult::from)
                .toList();

        return ExhibitionDetailsResult.builder()
                .id(exhibition.getId())
                .writer(writer)
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .tags(tagNames)
                .works(works)
                .viewCount(exhibition.getViewCount())
                .likeCount(exhibition.getLikeCount())
                .createdAt(exhibition.getCreatedAt())
                .isFollowing(isFollowing)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}
