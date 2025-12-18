package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCommentDetailResponse {
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

    public static ExhibitionCommentDetailResponse from(ExhibitionComment exhibitionComment) {
        return ExhibitionCommentDetailResponse.builder()
                .id(exhibitionComment.getId())
                .writer(Writer.from(exhibitionComment.getWriter()))
                .content(exhibitionComment.getContent())
                .createdAt(exhibitionComment.getCreatedAt())
                .build();
    }
}
