package com.benchpress200.photique.notification.application.query.result;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationView {
    private Long id;
    private String type;
    private Long targetId;
    @JsonProperty("isRead")
    @Getter(AccessLevel.NONE)
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationView from(Notification notification) {
        return NotificationView.builder()
                .id(notification.getId())
                .type(notification.getType().getValue())
                .build();
    }
}
