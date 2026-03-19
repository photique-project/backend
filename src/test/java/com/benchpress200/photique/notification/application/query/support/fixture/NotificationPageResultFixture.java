package com.benchpress200.photique.notification.application.query.support.fixture;

import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import com.benchpress200.photique.notification.application.query.result.NotificationView;
import java.util.List;

public class NotificationPageResultFixture {
    private NotificationPageResultFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int page = 0;
        private int size = 30;
        private long totalElements = 0L;
        private int totalPages = 0;
        private boolean isFirst = true;
        private boolean isLast = true;
        private boolean hasNext = false;
        private boolean hasPrevious = false;
        private List<NotificationView> notifications = List.of();
        private boolean unread = false;

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder isFirst(boolean isFirst) {
            this.isFirst = isFirst;
            return this;
        }

        public Builder isLast(boolean isLast) {
            this.isLast = isLast;
            return this;
        }

        public Builder hasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder hasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public Builder notifications(List<NotificationView> notifications) {
            this.notifications = notifications;
            return this;
        }

        public Builder unread(boolean unread) {
            this.unread = unread;
            return this;
        }

        public NotificationPageResult build() {
            return NotificationPageResult.builder()
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .isFirst(isFirst)
                    .isLast(isLast)
                    .hasNext(hasNext)
                    .hasPrevious(hasPrevious)
                    .notifications(notifications)
                    .unread(unread)
                    .build();
        }
    }
}
