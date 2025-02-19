package com.benchpress200.photique.singlework.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SingleWorkCommentDeleteRequest {
    private Long singleWorkId;
    private Long commentId;

    @NotNull(message = "Id must not be null")
    private Long writerId;

    public void withSingleWorkId(
            final Long singleWorkId
    ) {
        this.singleWorkId = singleWorkId;
    }

    public void withCommentId(
            final Long commentId
    ) {
        this.commentId = commentId;
    }
}
