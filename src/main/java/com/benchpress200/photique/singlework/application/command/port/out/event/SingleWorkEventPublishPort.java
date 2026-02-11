package com.benchpress200.photique.singlework.application.command.port.out.event;

import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;

public interface SingleWorkEventPublishPort {
    void publishSingleWorkImageUploadEvent(SingleWorkImageUploadEvent event);
}
