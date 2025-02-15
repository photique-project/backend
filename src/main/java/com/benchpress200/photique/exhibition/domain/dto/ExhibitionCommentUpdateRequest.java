package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ExhibitionCommentUpdateRequest {
    private Long exhibitionId;
    private Long commentId;

    @NotNull(message = "Id must not be null")
    @Id
    private Long writerId;

    @NotBlank(message = "Content must not be blank.")
    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;

    public void withExhibitionId(
            final Long exhibitionId
    ) {
        this.exhibitionId = exhibitionId;
    }

    public void withCommentId(
            final Long commentId
    ) {
        this.commentId = commentId;
    }
}
