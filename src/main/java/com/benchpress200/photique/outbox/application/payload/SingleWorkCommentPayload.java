package com.benchpress200.photique.outbox.application.payload;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkCommentPayload {
    private final Long id;
    private final Writer writer;
    private final Long singleWorkId;
    private final Long singleWorkWriterId;
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

    public static SingleWorkCommentPayload from(SingleWorkComment singleWorkComment) {
        return SingleWorkCommentPayload.builder()
                .id(singleWorkComment.getId())
                .writer(Writer.from(singleWorkComment.getWriter()))
                .singleWorkId(singleWorkComment.getSingleWork().getId())
                .singleWorkWriterId(singleWorkComment.getSingleWork().getWriter().getId())
                .content(singleWorkComment.getContent())
                .createdAt(singleWorkComment.getCreatedAt())
                .updatedAt(singleWorkComment.getUpdatedAt())
                .build();
    }
}
