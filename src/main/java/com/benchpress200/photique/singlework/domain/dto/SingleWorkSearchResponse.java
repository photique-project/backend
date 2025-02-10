package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkSearchResponse {
    private Long id;
    private Writer writer;
    private String image;
    private Long likeCount;
    private Long viewCount;

    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage
    ) {
        public static Writer from(final User writer) {
            return Writer.builder()
                    .id(writer.getId())
                    .nickname(writer.getNickname())
                    .profileImage(writer.getProfileImage())
                    .build();
        }

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

    public static SingleWorkSearchResponse from(final SingleWork singleWork) {
        return SingleWorkSearchResponse.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .image(singleWork.getImage())
                .likeCount(singleWork.getLikeCount())
                .viewCount(singleWork.getViewCount())
                .build();
    }

    public static SingleWorkSearchResponse from(final SingleWorkSearch singleWorkSearch) {
        return SingleWorkSearchResponse.builder()
                .id(singleWorkSearch.getId())
                .writer(Writer.of(singleWorkSearch.getWriterId(), singleWorkSearch.getWriterNickname(),
                        singleWorkSearch.getWriterProfileImage()))
                .image(singleWorkSearch.getImage())
                .likeCount(singleWorkSearch.getLikeCount())
                .viewCount(singleWorkSearch.getViewCount())
                .build();
    }

}
