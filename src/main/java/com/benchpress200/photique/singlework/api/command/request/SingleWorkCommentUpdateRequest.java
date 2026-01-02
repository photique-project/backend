package com.benchpress200.photique.singlework.api.command.request;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SingleWorkCommentUpdateRequest {
    @NotBlank(message = "Content must not be blank.")
    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;

    public SingleWorkCommentUpdateCommand toCommand(
            Long commentId
    ) {
        return SingleWorkCommentUpdateCommand.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }
}
