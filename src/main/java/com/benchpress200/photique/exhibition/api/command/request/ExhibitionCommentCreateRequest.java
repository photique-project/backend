package com.benchpress200.photique.exhibition.api.command.request;

import com.benchpress200.photique.exhibition.application.command.model.ExhibitionCommentCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ExhibitionCommentCreateRequest {
    @NotBlank(message = "Invalid content")
    @Size(min = 1, max = 300, message = "Invalid content")
    private String content;

    public ExhibitionCommentCreateCommand toCommand(Long exhibitionId) {
        return ExhibitionCommentCreateCommand.builder()
                .exhibitionId(exhibitionId)
                .content(content)
                .build();
    }
}
