package com.benchpress200.photique.singlework.application.command.port.out.event;

import com.benchpress200.photique.singlework.domain.event.SingleWorkCommentCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkDeleteEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkLikeAddEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkUpdateEvent;

public interface SingleWorkEventPublishPort {
    void publishSingleWorkImageUploadEvent(SingleWorkImageUploadEvent event);

    void publishSingleWorkCreateEvent(SingleWorkCreateEvent event);

    void publishSingleWorkUpdateEvent(SingleWorkUpdateEvent event);

    void publishSingleWorkDeleteEvent(SingleWorkDeleteEvent event);

    void publishSingleWorkLikeAddEvent(SingleWorkLikeAddEvent event);

    void publishSingleWorkCommentCreateEvent(SingleWorkCommentCreateEvent event);
}
