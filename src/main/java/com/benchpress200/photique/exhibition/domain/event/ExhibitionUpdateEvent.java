package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionUpdateEvent {
    private Long exhibitionId;

    private ExhibitionUpdateEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionUpdateEvent of(Long exhibitionId) {
        return new ExhibitionUpdateEvent(exhibitionId);
    }
}
