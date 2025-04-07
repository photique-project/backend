package com.benchpress200.photique.exhibition.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExhibitionDetailRequest {
    private Long userId;
    private Long exhibitionId;

    public void withExhibitionId(final Long exhibitionId) {
        this.exhibitionId = exhibitionId;

    }
}
