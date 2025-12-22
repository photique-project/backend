package com.benchpress200.photique.image.domain.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishImageDeleteEventIfRollback(String imageUrl) {
        ImageUploadRollbackEvent event = new ImageUploadRollbackEvent(imageUrl);
        publisher.publishEvent(event);
    }

    public void publishImageDeleteEventIfCommit(String imageUrl) {
        ImageDeleteCommitEvent event = new ImageDeleteCommitEvent(imageUrl);
        publisher.publishEvent(event);
    }
}
