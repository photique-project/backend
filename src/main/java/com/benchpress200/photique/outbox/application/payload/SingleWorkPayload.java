package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkPayload {
    private final Long id;
    private final Writer writer;
    private final String title;
    private final String description;
    private final List<String> tags;
    private final String image;
    private final String category;
    private final Long viewCount;
    private final Long likeCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Getter
    @Builder
    private static class Writer {
        private final Long id;
        private final String nickname;
        private final String profileImage;

        private static Writer from(User user) {
            return Writer.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .build();
        }
    }

    public static SingleWorkPayload of(
            SingleWork singleWork,
            List<String> tagNames
    ) {
        return SingleWorkPayload.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .title(singleWork.getTitle())
                .description(singleWork.getDescription())
                .tags(tagNames)
                .image(singleWork.getImage())
                .category(singleWork.getCategory().getValue())
                .viewCount(singleWork.getViewCount())
                .likeCount(singleWork.getLikeCount())
                .createdAt(singleWork.getCreatedAt())
                .updatedAt(singleWork.getUpdatedAt())
                .build();
    }

    public static SingleWorkPayload of(Long id) {
        return SingleWorkPayload.builder()
                .id(id)
                .build();
    }

    public static SingleWorkPayload of(SingleWork singleWork) {
        return SingleWorkPayload.builder()
                .id(singleWork.getId())
                .writer(Writer.from(singleWork.getWriter()))
                .likeCount(singleWork.getLikeCount())
                .build();
    }

}
