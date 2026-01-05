package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionLikeAddEvent {
    private final Long exhibitionId;

    private ExhibitionLikeAddEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionLikeAddEvent of(Long exhibitionId) {
        return new ExhibitionLikeAddEvent(exhibitionId);
    }
}
