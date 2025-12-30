package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkCreateEvent {
    private Long singleWorkId;

    private SingleWorkCreateEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkCreateEvent of(Long singleWorkId) {
        return new SingleWorkCreateEvent(singleWorkId);
    }
}
