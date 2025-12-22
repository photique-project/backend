package com.benchpress200.photique.image.domain.event;

import com.benchpress200.photique.image.domain.port.ImageUploaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageEventListener {
    private final ImageUploaderPort imageUploaderPort;

    // 이미지를 업로드했던 트랜잭션이 롤백되면 S3에 업로드했던 이미지 제거
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleImageDeleteEventIfRollback(ImageUploadRollbackEvent imageUploadRollbackEvent) {
        String imageUrl = imageUploadRollbackEvent.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }

    // 이미지 제거 트랜잭션(회원탈퇴, 단일작품 삭제, 전시회 삭제)이 커밋되면 S3에 업로드했던 프로필 이미지 제거
    // 커밋이 되지않고 바로 제거한다면 해당 트랜잭션이 롤백됐을 때
    // 해당 이미지를 다시 살릴 방법이 없으므로 트랜잭션 커밋 이벤트가 확정되면 삭제처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handImageDeleteEventIfCommit(ImageDeleteCommitEvent imageDeleteCommitEvent) {
        String imageUrl = imageDeleteCommitEvent.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }
}
