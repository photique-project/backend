package com.benchpress200.photique.notification.domain.dto;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private Long targetId;
    private boolean isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static NotificationResponse from(final Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().getValue())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
