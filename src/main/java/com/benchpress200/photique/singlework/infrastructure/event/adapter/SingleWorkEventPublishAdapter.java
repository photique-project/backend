package com.benchpress200.photique.singlework.infrastructure.event.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCommentCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkDeleteEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkLikeAddEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleWorkEventPublishAdapter implements SingleWorkEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishSingleWorkImageUploadEvent(SingleWorkImageUploadEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishSingleWorkCreateEvent(SingleWorkCreateEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishSingleWorkUpdateEvent(SingleWorkUpdateEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishSingleWorkDeleteEvent(SingleWorkDeleteEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishSingleWorkLikeAddEvent(SingleWorkLikeAddEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishSingleWorkCommentCreateEvent(SingleWorkCommentCreateEvent event) {
        publisher.publishEvent(event);
    }
}
