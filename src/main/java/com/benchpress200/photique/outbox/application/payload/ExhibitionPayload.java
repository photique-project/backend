package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionPayload {
    private Long id;
    private Writer writer;
    private String cardColor;
    private String title;
    private String description;
    private List<String> tags;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExhibitionPayload of(
            Exhibition exhibition,
            List<String> tagNames
    ) {
        return ExhibitionPayload.builder()
                .id(exhibition.getId())
                .writer(Writer.from(exhibition.getWriter()))
                .cardColor(exhibition.getCardColor())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .tags(tagNames)
                .viewCount(exhibition.getViewCount())
                .likeCount(exhibition.getLikeCount())
                .createdAt(exhibition.getCreatedAt())
                .updatedAt(exhibition.getUpdatedAt())
                .build();
    }

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
}
