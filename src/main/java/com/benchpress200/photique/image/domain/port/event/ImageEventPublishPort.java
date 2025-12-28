package com.benchpress200.photique.image.domain.port.event;

import com.benchpress200.photique.image.domain.event.DeleteImageEvent;
import com.benchpress200.photique.image.domain.event.UploadImageEvent;

public interface ImageEventPublishPort {
    void publishUploadImageEvent(UploadImageEvent uploadImageEvent);

    void publishDeleteImageEvent(DeleteImageEvent deleteImageEvent);
}
