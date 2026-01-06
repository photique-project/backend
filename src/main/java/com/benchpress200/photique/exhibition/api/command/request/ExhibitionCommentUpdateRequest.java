package com.benchpress200.photique.exhibition.api.command.request;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ExhibitionCommentUpdateRequest {
    @NotBlank(message = "Invalid content")
    @Size(min = 1, max = 300, message = "Invalid content")
    private String content;

    public ExhibitionCommentUpdateCommand toCommand(Long commentId) {
        return ExhibitionCommentUpdateCommand.builder()
                .commentId(commentId)
                .content(content)
                .build();
    }
}
