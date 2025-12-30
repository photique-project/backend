package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkUpdateEvent {
    private Long singleWorkId;

    private SingleWorkUpdateEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkUpdateEvent of(Long singleWorkId) {
        return new SingleWorkUpdateEvent(singleWorkId);
    }
}
