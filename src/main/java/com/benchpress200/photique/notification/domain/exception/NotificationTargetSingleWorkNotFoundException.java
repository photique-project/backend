package com.benchpress200.photique.notification.domain.exception;

public class NotificationTargetSingleWorkNotFoundException extends RuntimeException {
    public NotificationTargetSingleWorkNotFoundException(Long singleWorkId) {
        super("Singlework with id [" + singleWorkId + "] not found for notification");
    }
}
