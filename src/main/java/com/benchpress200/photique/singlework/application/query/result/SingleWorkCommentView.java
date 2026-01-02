package com.benchpress200.photique.singlework.application.query.result;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkCommentView {
    private Long id;
    private Writer writer;
    private String content;
    private LocalDateTime createdAt;

    public static SingleWorkCommentView from(SingleWorkComment singleWorkComment) {
        return SingleWorkCommentView.builder()
                .id(singleWorkComment.getId())
                .writer(Writer.from(singleWorkComment.getWriter()))
                .content(singleWorkComment.getContent())
                .createdAt(singleWorkComment.getCreatedAt())
                .build();
    }
}
