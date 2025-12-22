package com.benchpress200.photique.notification.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewSingleWorkNotificationCommitEvent {
    private Long singleWorkId;
}
