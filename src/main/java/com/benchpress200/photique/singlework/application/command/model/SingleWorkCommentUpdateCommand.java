package com.benchpress200.photique.singlework.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleWorkCommentUpdateCommand {
    private Long commentId;
    private String content;
}
