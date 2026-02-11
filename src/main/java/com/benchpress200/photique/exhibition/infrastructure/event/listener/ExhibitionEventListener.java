package com.benchpress200.photique.exhibition.infrastructure.event.listener;

import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ExhibitionEventListener {
    private final ImageUploaderPort imageUploaderPort;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleExhibitionWorkImageUploadEventIfRollback(ExhibitionWorkImageUploadEvent event) {
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }
}
