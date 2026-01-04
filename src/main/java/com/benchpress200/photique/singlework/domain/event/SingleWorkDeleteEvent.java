package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkDeleteEvent {
    private Long singleWorkId;

    private SingleWorkDeleteEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkDeleteEvent of(Long singleWorkId) {
        return new SingleWorkDeleteEvent(singleWorkId);
    }
}
