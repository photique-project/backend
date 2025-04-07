package com.benchpress200.photique.notification.presentation;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.interceptor.Auth;
import com.benchpress200.photique.common.response.ApiSuccessResponse;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.notification.application.NotificationService;
import com.benchpress200.photique.notification.domain.dto.CountUnreadResponse;
import com.benchpress200.photique.notification.domain.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.NOTIFICATION_DOMAIN)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(URL.SUB)
    public SseEmitter subscribe(@PathVariable("userId") Long userId) {
        return notificationService.subscribe(userId);
    }

    @Auth
    @GetMapping
    public ApiSuccessResponse<?> getNotifications(
            @PathVariable("userId") final Long userId,
            final Pageable pageable
    ) {
        Page<NotificationResponse> notificationRequestPage = notificationService.getNotifications(userId, pageable);
        return ResponseHandler.handleSuccessResponse(notificationRequestPage, HttpStatus.OK);
    }

    @Auth
    @PatchMapping(URL.NOTIFICATION_DATA)
    public ApiSuccessResponse<?> markAsRead(
            @PathVariable("userId") final Long userId,
            @PathVariable("notificationId") final Long notificationId
    ) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @PatchMapping
    public ApiSuccessResponse<?> markAllAsRead(
            @PathVariable("userId") final Long userId
    ) {
        notificationService.markAllAsRead(userId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @DeleteMapping(URL.NOTIFICATION_DATA)
    public ApiSuccessResponse<?> deleteNotification(
            @PathVariable("userId") final Long userId,
            @PathVariable("notificationId") final Long notificationId
    ) {
        notificationService.deleteNotification(userId, notificationId);
        return ResponseHandler.handleSuccessResponse(HttpStatus.NO_CONTENT);
    }

    @Auth
    @GetMapping(URL.UNREAD)
    public ApiSuccessResponse<?> countUnread(
            @PathVariable("userId") final Long userId
    ) {
        CountUnreadResponse countUnreadResponse = notificationService.countUnread(userId);
        return ResponseHandler.handleSuccessResponse(countUnreadResponse, HttpStatus.OK);
    }
}
