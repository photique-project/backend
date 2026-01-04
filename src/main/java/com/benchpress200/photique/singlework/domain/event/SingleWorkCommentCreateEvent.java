package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkCommentCreateEvent {
    private Long singleWorkId;

    private SingleWorkCommentCreateEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkCommentCreateEvent of(Long singleWorkId) {
        return new SingleWorkCommentCreateEvent(singleWorkId);
    }
}
