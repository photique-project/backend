package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionBookmarkAddEvent {
    private final Long exhibitionId;

    private ExhibitionBookmarkAddEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionBookmarkAddEvent of(Long exhibitionId) {
        return new ExhibitionBookmarkAddEvent(exhibitionId);
    }
}
