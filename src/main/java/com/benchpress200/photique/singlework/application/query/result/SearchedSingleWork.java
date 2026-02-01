package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchedSingleWork {
    private Long id;
    private Writer writer;
    private String image;
    private Long likeCount;
    private Long viewCount;
    @JsonProperty("isLiked")
    @Getter(AccessLevel.NONE)
    private boolean isLiked;

    public static SearchedSingleWork of(
            SingleWorkSearch singleWorkSearch,
            boolean isLiked
    ) {

        Long writerId = singleWorkSearch.getWriterId();
        String nickname = singleWorkSearch.getWriterNickname();
        String profileImage = singleWorkSearch.getWriterProfileImage();

        return SearchedSingleWork.builder()
                .id(singleWorkSearch.getId())
                .writer(
                        Writer.of(
                                writerId,
                                nickname,
                                profileImage
                        )
                )
                .image(singleWorkSearch.getImage())
                .likeCount(singleWorkSearch.getLikeCount())
                .viewCount(singleWorkSearch.getViewCount())
                .isLiked(isLiked)
                .build();
    }

    public static SearchedSingleWork of(
            SingleWork singleWork,
            boolean isLiked
    ) {
        return SearchedSingleWork.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .image(singleWork.getImage())
                .likeCount(singleWork.getLikeCount())
                .viewCount(singleWork.getViewCount())
                .isLiked(isLiked)
                .build();
    }

    public static SearchedSingleWork from(SingleWorkLike singleWorkLike) {
        SingleWork singleWork = singleWorkLike.getSingleWork();
        User writer = singleWork.getWriter();

        return SearchedSingleWork.builder()
                .id(singleWork.getId())
                .writer(Writer.from(writer))
                .image(singleWork.getImage())
                .viewCount(singleWork.getViewCount())
                .likeCount(singleWork.getLikeCount())
                .isLiked(true)
                .build();
    }
}
