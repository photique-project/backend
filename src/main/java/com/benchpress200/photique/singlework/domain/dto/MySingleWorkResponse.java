package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySingleWorkResponse {
    private Long id;
    private Writer writer;
    private String image;
    private Long likeCount;
    private Long viewCount;
    @JsonProperty("isLiked")
    private boolean isLiked;

    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage
    ) {
        public static Writer of(
                final Long id,
                final String nickname,
                final String profileImage
        ) {
            return Writer.builder()
                    .id(id)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        }
    }

    public static MySingleWorkResponse of(
            final SingleWorkSearch singleWorkSearch,
            final boolean isLiked
    ) {
        return MySingleWorkResponse.builder()
                .id(singleWorkSearch.getId())
                .writer(Writer.of(singleWorkSearch.getWriterId(), singleWorkSearch.getWriterNickname(),
                        singleWorkSearch.getWriterProfileImage()))
                .image(singleWorkSearch.getImage())
                .likeCount(singleWorkSearch.getLikeCount())
                .viewCount(singleWorkSearch.getViewCount())
                .isLiked(isLiked)
                .build();
    }
}
