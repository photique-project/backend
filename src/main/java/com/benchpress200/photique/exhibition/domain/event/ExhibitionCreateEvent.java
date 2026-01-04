package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionCreateEvent {
    private Long exhibitionId;

    private ExhibitionCreateEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionCreateEvent of(Long exhibitionId) {
        return new ExhibitionCreateEvent(exhibitionId);
    }
}
