package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkRemoveEvent {
    private Long singleWorkId;

    private SingleWorkRemoveEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkRemoveEvent of(Long singleWorkId) {
        return new SingleWorkRemoveEvent(singleWorkId);
    }
}
