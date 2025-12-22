package com.benchpress200.photique.singlework.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleWorkSearchEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishCreateSingleWorkSearchEvent(Long singleWorkId) {
        CreateSingleWorkSearchEvent event = new CreateSingleWorkSearchEvent(singleWorkId);
        publisher.publishEvent(event);
    }

    public void publishUpdateSingleWorkSearchEvent(Long singleWorkId) {
        UpdateSingleWorkSearchEvent event = new UpdateSingleWorkSearchEvent(singleWorkId);
        publisher.publishEvent(event);
    }

    public void publishRemoveSingleWorkSearchEvent(Long singleWorkId) {
        RemoveSingleWorkSearchEvent event = new RemoveSingleWorkSearchEvent(singleWorkId);
        publisher.publishEvent(event);
    }
}
