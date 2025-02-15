package com.benchpress200.photique.exhibition.domain.dto;

import com.benchpress200.photique.common.dtovalidator.annotation.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionBookmarkRemoveRequest {
    @NotNull(message = "User's id must not be null")
    @Id
    private Long userId;
    private Long exhibitionId;

    public void withExhibitionId(
            final Long exhibitionId
    ) {
        this.exhibitionId = exhibitionId;
    }
}

