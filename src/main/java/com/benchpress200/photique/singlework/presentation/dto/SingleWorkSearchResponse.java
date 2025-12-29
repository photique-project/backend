package com.benchpress200.photique.singlework.presentation.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleWorkSearchResponse {
    private Long id;
    private Writer writer;
    private String image;
    private Long likeCount;
    private Long viewCount;
    @JsonProperty("isLiked")
    private boolean liked;

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

        public static Writer of(
                Long id,
                String nickname,
                String profileImage
        ) {
            return Writer.builder()
                    .id(id)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .build();
        }
    }

    public static SingleWorkSearchResponse from(SingleWork singleWork, Long likeCount) {
        return SingleWorkSearchResponse.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .image(singleWork.getImage())
                .likeCount(likeCount)
                .viewCount(singleWork.getViewCount())
                .build();
    }

    public static SingleWorkSearchResponse of(
            SingleWorkSearch singleWorkSearch,
            boolean isLiked
    ) {
        return SingleWorkSearchResponse.builder()
                .id(singleWorkSearch.getId())
                .writer(Writer.of(singleWorkSearch.getWriterId(), singleWorkSearch.getWriterNickname(),
                        singleWorkSearch.getWriterProfileImage()))
                .image(singleWorkSearch.getImage())
                .likeCount(singleWorkSearch.getLikeCount())
                .viewCount(singleWorkSearch.getViewCount())
                .liked(isLiked)
                .build();
    }
}
