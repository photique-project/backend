package com.benchpress200.photique.singlework.infrastructure.event.adapter;

import com.benchpress200.photique.singlework.application.command.port.out.event.SingleWorkEventPublishPort;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkRemoveEvent;
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
    public void publishSingleWorkRemoveEvent(SingleWorkRemoveEvent event) {
        publisher.publishEvent(event);
    }
}
