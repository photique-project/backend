package com.benchpress200.photique.singlework.infrastructure.event.listener;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SingleWorkEventListener {
    private final ImageUploaderPort imageUploaderPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleSingleWorkImageUploadEventIfRollback(SingleWorkImageUploadEvent event) {
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }
}
