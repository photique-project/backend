package com.benchpress200.photique.singlework.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SingleWorkSearchEventPublisher {
    private final ApplicationEventPublisher publisher;
    
    public void publishSingleWorkSearchCreationEventIfCommit(Long singleWorkId) {
        SingleWorkSearchCreationCommitEvent event = new SingleWorkSearchCreationCommitEvent(singleWorkId);
        publisher.publishEvent(event);
    }
}
