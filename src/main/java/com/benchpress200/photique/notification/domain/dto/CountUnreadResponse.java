package com.benchpress200.photique.notification.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountUnreadResponse {
    private long unreadCount;

    public static CountUnreadResponse from(long count) {
        return CountUnreadResponse.builder()
                .unreadCount(count)
                .build();
    }
}
