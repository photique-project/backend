package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionDetailsResponse {
    private Long id;
    private Writer writer;
    private String title;
    private String description;
    private List<Work> works;
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
        public static Writer from(
                final User user
        ) {
            return Writer.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .introduction(user.getIntroduction())
                    .build();
        }
    }

    @Builder
    record Work(
            String image,
            String title,
            String description
    ) {
        public static Work from(
                final ExhibitionWork exhibitionWork
        ) {
            return Work.builder()
                    .image(exhibitionWork.getImage())
                    .title(exhibitionWork.getTitle())
                    .description(exhibitionWork.getDescription())
                    .build();
        }

    }

    public static ExhibitionDetailsResponse of(
            final Exhibition exhibition,
            final List<ExhibitionWork> exhibitionWorks,
            final boolean isLiked,
            final boolean isBookmarked
    ) {
        List<Work> works = exhibitionWorks.stream()
                .map(Work::from)
                .toList();

        return ExhibitionDetailsResponse.builder()
                .id(exhibition.getId())
                .writer(Writer.from(exhibition.getWriter()))
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .works(works)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }
}
