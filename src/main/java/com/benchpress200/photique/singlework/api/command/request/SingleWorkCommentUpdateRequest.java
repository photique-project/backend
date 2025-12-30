package com.benchpress200.photique.singlework.api.command.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SingleWorkCommentUpdateRequest {
    private Long singleWorkId;
    private Long commentId;

    @NotNull(message = "Id must not be null")
    private Long writerId;

    @NotBlank(message = "Content must not be blank.")
    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;

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
