package com.benchpress200.photique.notification.application.query.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class NotificationPageQuery {
    private Pageable pageable;
}
