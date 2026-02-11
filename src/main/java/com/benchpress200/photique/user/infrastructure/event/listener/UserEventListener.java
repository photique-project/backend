package com.benchpress200.photique.user.infrastructure.event.listener;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.user.domain.event.UserProfileImageDeleteEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final ImageUploaderPort imageUploaderPort;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleUserProfileImageUploadEventIfRollback(UserProfileImageUploadEvent event) {
        // 이미지 삭제 처리 실패하면 전역 예외 핸들러에서 로깅
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserProfileImageDeleteEventIfCommit(UserProfileImageDeleteEvent event) {
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }
}
