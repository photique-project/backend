package com.benchpress200.photique.exhibition.application.command.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCommentUpdateCommand {
    private Long commentId;
    private String content;
}
