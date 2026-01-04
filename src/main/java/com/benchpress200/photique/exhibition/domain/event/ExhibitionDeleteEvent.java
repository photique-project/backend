package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionDeleteEvent {
    private Long exhibitionId;

    private ExhibitionDeleteEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionDeleteEvent of(Long exhibitionId) {
        return new ExhibitionDeleteEvent(exhibitionId);
    }
}
