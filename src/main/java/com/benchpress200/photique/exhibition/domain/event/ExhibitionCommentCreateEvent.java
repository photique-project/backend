package com.benchpress200.photique.exhibition.domain.event;

import lombok.Getter;

@Getter
public class ExhibitionCommentCreateEvent {
    private final Long exhibitionId;

    private ExhibitionCommentCreateEvent(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public static ExhibitionCommentCreateEvent of(Long exhibitionId) {
        return new ExhibitionCommentCreateEvent(exhibitionId);
    }
}
