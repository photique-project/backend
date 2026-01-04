package com.benchpress200.photique.singlework.domain.event;

import lombok.Getter;

@Getter
public class SingleWorkLikeAddEvent {
    private Long singleWorkId;

    private SingleWorkLikeAddEvent(Long singleWorkId) {
        this.singleWorkId = singleWorkId;
    }

    public static SingleWorkLikeAddEvent of(Long singleWorkId) {
        return new SingleWorkLikeAddEvent(singleWorkId);
    }
}
