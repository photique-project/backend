package com.benchpress200.photique.exhibition.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExhibitionLikeDecrementRequest {
    @NotNull(message = "User's id must not be null")
    private Long userId;
    private Long exhibitionId;

    public void withExhibitionId(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }
}
