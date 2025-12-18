package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularSingleWorkResponse {
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
        public static Writer from(User user) {
            return Writer.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();
        }
    }

    public static PopularSingleWorkResponse of(
            SingleWork singleWork,
            Long likeCount,
            boolean isLiked
    ) {
        return PopularSingleWorkResponse.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .image(singleWork.getImage())
                .likeCount(likeCount)
                .viewCount(singleWork.getViewCount())
                .isLiked(isLiked)
                .build();
    }
}
