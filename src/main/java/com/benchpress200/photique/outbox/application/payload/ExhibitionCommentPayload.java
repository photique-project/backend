package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCommentPayload {
    private final Long id;
    private final Writer writer;
    private final Long exhibitionId;
    private final Long exhibitionWriterId;
    private final String content;
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

    public static ExhibitionCommentPayload from(ExhibitionComment exhibitionComment) {
        return ExhibitionCommentPayload.builder()
                .id(exhibitionComment.getId())
                .writer(Writer.from(exhibitionComment.getWriter()))
                .exhibitionId(exhibitionComment.getExhibition().getId())
                .exhibitionWriterId(exhibitionComment.getExhibition().getWriter().getId())
                .content(exhibitionComment.getContent())
                .createdAt(exhibitionComment.getCreatedAt())
                .updatedAt(exhibitionComment.getUpdatedAt())
                .build();
    }
}
