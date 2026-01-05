package com.benchpress200.photique.exhibition.application.command.model;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExhibitionCommentCreateCommand {
    private Long exhibitionId;
    private String content;

    public ExhibitionComment toEntity(
            User writer,
            Exhibition exhibition
    ) {
        return ExhibitionComment.builder()
                .writer(writer)
                .exhibition(exhibition)
                .content(content)
                .build();
    }
}
