package com.benchpress200.photique.notification.application.query.result;

import com.benchpress200.photique.notification.domain.entity.Notification;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class NotificationPageResult {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<NotificationView> notifications;
    private boolean unread;

    public static NotificationPageResult of(
            Page<Notification> notificationPage,
            boolean unread
    ) {
        List<NotificationView> notifications = notificationPage.stream()
                .map(NotificationView::from)
                .toList();

        return NotificationPageResult.builder()
                .page(notificationPage.getNumber())
                .size(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .isFirst(notificationPage.isFirst())
                .isLast(notificationPage.isLast())
                .hasNext(notificationPage.hasNext())
                .hasPrevious(notificationPage.hasPrevious())
                .notifications(notifications)
                .unread(unread)
                .build();
    }
}
