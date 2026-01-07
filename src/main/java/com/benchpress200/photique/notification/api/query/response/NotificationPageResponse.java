package com.benchpress200.photique.notification.api.query.response;

import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import com.benchpress200.photique.notification.application.query.result.NotificationView;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationPageResponse {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    @JsonProperty("isFirst")
    @Getter(AccessLevel.NONE)
    private boolean isFirst;
    @JsonProperty("isLast")
    @Getter(AccessLevel.NONE)
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<NotificationView> notifications;
    private boolean unread;

    public static NotificationPageResponse from(NotificationPageResult result) {
        return NotificationPageResponse.builder()
                .page(result.getPage())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .isFirst(result.isFirst())
                .isLast(result.isLast())
                .hasNext(result.isHasNext())
                .hasPrevious(result.isHasPrevious())
                .notifications(result.getNotifications())
                .unread(result.isUnread())
                .build();
    }
}
