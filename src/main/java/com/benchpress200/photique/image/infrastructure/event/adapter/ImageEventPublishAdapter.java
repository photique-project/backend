package com.benchpress200.photique.image.infrastructure.event.adapter;

import com.benchpress200.photique.image.domain.event.DeleteImageEvent;
import com.benchpress200.photique.image.domain.event.UploadImageEvent;
import com.benchpress200.photique.image.domain.port.event.ImageEventPublishPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageEventPublishAdapter implements ImageEventPublishPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishUploadImageEvent(UploadImageEvent uploadImageEvent) {
        publisher.publishEvent(uploadImageEvent);
    }

    @Override
    public void publishDeleteImageEvent(DeleteImageEvent deleteImageEvent) {
        publisher.publishEvent(deleteImageEvent);
    }
}
