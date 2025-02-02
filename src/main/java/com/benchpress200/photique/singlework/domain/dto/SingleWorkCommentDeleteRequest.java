package com.benchpress200.photique.singlework.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SingleWorkCommentDeleteRequest {
    private Long singleWorkId;
    private Long commentId;

    @NotNull(message = "Id must not be null")
    @Id
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
