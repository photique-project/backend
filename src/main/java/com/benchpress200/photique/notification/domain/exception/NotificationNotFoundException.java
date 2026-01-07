package com.benchpress200.photique.notification.domain.exception;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long notificationId) {
        super(String.format("Notification with id [%s] not found", notificationId));
    }
}
