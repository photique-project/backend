package com.benchpress200.photique.notification.application.query.support.fixture;

import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class NotificationPageQueryFixture {
    private NotificationPageQueryFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Pageable pageable = PageRequest.of(0, 30);

        public Builder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public NotificationPageQuery build() {
            return NotificationPageQuery.builder()
                    .pageable(pageable)
                    .build();
        }
    }
}
