package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkCommentDetailResponse {
    private Long id;
    private Writer writer;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @Builder
    record Writer(
            Long id,
            String nickname,
            String profileImage
    ) {
        public static Writer from(User writer) {
            return Writer.builder()
                    .id(writer.getId())
                    .nickname(writer.getNickname())
                    .profileImage(writer.getProfileImage())
                    .build();
        }
    }

    public static SingleWorkCommentDetailResponse from(SingleWorkComment singleWorkComment) {
        return SingleWorkCommentDetailResponse.builder()
                .id(singleWorkComment.getId())
                .writer(Writer.from(singleWorkComment.getWriter()))
                .content(singleWorkComment.getContent())
                .createdAt(singleWorkComment.getCreatedAt())
                .build();
    }
}
