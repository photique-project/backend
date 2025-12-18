package com.benchpress200.photique.exhibition.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExhibitionCommentDeleteRequest {
    private Long exhibitionId;
    private Long commentId;

    @NotNull(message = "Id must not be null")
    private Long writerId;

    public void withExhibitionId(
            Long exhibitionId
    ) {
        this.exhibitionId = exhibitionId;
    }

    public void withCommentId(
            Long commentId
    ) {
        this.commentId = commentId;
    }
}
