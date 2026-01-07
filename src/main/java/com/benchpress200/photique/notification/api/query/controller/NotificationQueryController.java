package com.benchpress200.photique.notification.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.notification.api.query.contant.NotificationQueryResponseMessage;
import com.benchpress200.photique.notification.api.query.request.NotificationPageRequest;
import com.benchpress200.photique.notification.api.query.response.NotificationPageResponse;
import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import com.benchpress200.photique.notification.application.query.port.in.GetNotificationPageUserCase;
import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationQueryController {
    private final GetNotificationPageUserCase getNotificationPageUserCase;

    @GetMapping(ApiPath.NOTIFICATION_ROOT)
    public ResponseEntity<?> getNotificationPage(
            @ModelAttribute @Valid NotificationPageRequest request
    ) {
        NotificationPageQuery query = request.toQuery();
        NotificationPageResult result = getNotificationPageUserCase.getNotificationPage(query);
        NotificationPageResponse response = NotificationPageResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                NotificationQueryResponseMessage.NOTIFICATION_FETCH_SUCCESS,
                response
        );
    }
}
