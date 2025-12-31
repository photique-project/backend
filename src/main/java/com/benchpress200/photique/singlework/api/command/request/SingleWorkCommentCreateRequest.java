package com.benchpress200.photique.singlework.api.command.request;

import com.benchpress200.photique.singlework.application.command.model.SingleWorkCommentCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SingleWorkCommentCreateRequest {
    @NotBlank(message = "Invalid content")
    @Size(min = 1, max = 300, message = "Invalid content")
    private String content;


    public SingleWorkCommentCreateCommand toCommand(Long singleWorkId) {
        return SingleWorkCommentCreateCommand.builder()
                .singleWorkId(singleWorkId)
                .content(content)
                .build();
    }
}

