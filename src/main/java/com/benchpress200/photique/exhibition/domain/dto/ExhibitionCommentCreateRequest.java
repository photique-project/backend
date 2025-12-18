package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.user.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ExhibitionCommentCreateRequest {
    private Long exhibitionId;

    @NotNull(message = "Id must not be null")
    private Long writerId;

    @NotBlank(message = "Content must not be blank.")
    @Size(max = 200, message = "Content must not exceed 200 characters")
    private String content;

    public void withExhibitionId(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public ExhibitionComment toEntity(
            Exhibition exhibition,
            User user
    ) {
        return ExhibitionComment.builder()
                .exhibition(exhibition)
                .writer(user)
                .content(content)
                .build();
    }
}
