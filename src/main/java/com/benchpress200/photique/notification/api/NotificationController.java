package com.benchpress200.photique.notification.api;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.notification.application.NotificationService;
import com.benchpress200.photique.notification.domain.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping(ApiPath.NOTIFICATION_ROOT)
    public ApiSuccessResponse<?> getNotifications(
            @PathVariable("userId") final Long userId,
            final Pageable pageable
    ) {
        Page<NotificationResponse> notificationRequestPage = notificationService.getNotifications(userId, pageable);
        return ResponseHandler.handleSuccessResponse(notificationRequestPage, HttpStatus.OK);
    }

    @PatchMapping(ApiPath.NOTIFICATION_DATA)
    public ApiSuccessResponse<?> markAsRead(
            @PathVariable("userId") final Long userId,
            @PathVariable("notificationId") final Long notificationId
    ) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(ApiPath.NOTIFICATION_ROOT)
    public ApiSuccessResponse<?> markAllAsRead(
            @PathVariable("userId") final Long userId
    ) {
        notificationService.markAllAsRead(userId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(ApiPath.NOTIFICATION_DATA)
    public ApiSuccessResponse<?> deleteNotification(
            @PathVariable("userId") final Long userId,
            @PathVariable("notificationId") final Long notificationId
    ) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }
}
