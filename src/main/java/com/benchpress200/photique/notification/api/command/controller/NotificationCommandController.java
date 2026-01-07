package com.benchpress200.photique.notification.api.command.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.notification.application.command.port.in.DeleteNotificationUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAllAsReadUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAsReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationCommandController {
    private final MarkAsReadUseCase markAsReadUseCase;
    private final MarkAllAsReadUseCase markAllAsReadUseCase;
    private final DeleteNotificationUseCase deleteNotificationUseCase;

    @PatchMapping(ApiPath.NOTIFICATION_DATA)
    public ResponseEntity<?> markAsRead(
            @PathVariable(PathVariableName.NOTIFICATION_ID) Long notificationId
    ) {
        markAsReadUseCase.markAsRead(notificationId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    @PatchMapping(ApiPath.NOTIFICATION_ROOT)
    public ResponseEntity<?> markAllAsRead() {
        markAllAsReadUseCase.markAllAsRead();

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }

    @DeleteMapping(ApiPath.NOTIFICATION_DATA)
    public ResponseEntity<?> deleteNotification(
            @PathVariable(PathVariableName.NOTIFICATION_ID) Long notificationId
    ) {
        deleteNotificationUseCase.deleteNotification(notificationId);

        return ResponseHandler.handleResponse(
                HttpStatus.NO_CONTENT
        );
    }
}
