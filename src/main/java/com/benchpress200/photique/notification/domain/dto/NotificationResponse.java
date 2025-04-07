package com.benchpress200.photique.notification.domain.dto;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String type;
    private Long targetId;
    @JsonProperty("isRead")
    private boolean isRead;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static NotificationResponse from(final Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType().getValue())
                .targetId(notification.getTargetId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
