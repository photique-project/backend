package com.benchpress200.photique.notification.domain.support;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;

public class NotificationFixture {
    private NotificationFixture() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User receiver = UserFixture.builder().id(1L).build();
        private NotificationType type = NotificationType.FOLLOW;
        private Long targetId = 1L;

        public Builder receiver(User receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder targetId(Long targetId) {
            this.targetId = targetId;
            return this;
        }

        public Notification build() {
            return Notification.builder()
                    .receiver(receiver)
                    .type(type)
                    .targetId(targetId)
                    .build();
        }
    }
}
