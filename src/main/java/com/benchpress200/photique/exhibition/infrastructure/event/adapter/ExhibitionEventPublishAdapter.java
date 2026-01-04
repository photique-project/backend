package com.benchpress200.photique.exhibition.infrastructure.event.adapter;

import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionEventPublishPort;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionCreateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionUpdateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionEventPublishAdapter implements ExhibitionEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishExhibitionWorkImageUploadEvent(ExhibitionWorkImageUploadEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishExhibitionCreateEvent(ExhibitionCreateEvent event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publishExhibitionUpdateEvent(ExhibitionUpdateEvent event) {
        publisher.publishEvent(event);
    }
}
