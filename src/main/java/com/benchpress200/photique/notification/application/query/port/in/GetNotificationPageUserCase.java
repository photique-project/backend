package com.benchpress200.photique.notification.application.query.port.in;

import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;

public interface GetNotificationPageUserCase {
    NotificationPageResult getNotificationPage(NotificationPageQuery query);
}
