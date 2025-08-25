package com.benchpress200.photique.user.domain.event;

import com.benchpress200.photique.image.domain.ImageUploaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final ImageUploaderPort imageUploaderPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterCreateUserRollback(final DeleteProfileImageEvent deleteProfileImageEvent) {
        // 회원가입 때 트랜잭션이 롤백되면 S3에 업로드했던 프로필 이미지 제거
        String profileImageUrl = deleteProfileImageEvent.getProfileImageUrl();
        imageUploaderPort.delete(profileImageUrl);
    }
}
