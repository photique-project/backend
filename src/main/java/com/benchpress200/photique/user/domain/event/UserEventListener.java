package com.benchpress200.photique.user.domain.event;

import com.benchpress200.photique.user.domain.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private final UserSearchRepository userSearchRepository;

    /**
     * 이미지를 업로드했던 트랜잭션이 롤백되면 S3에 업로드했던 이미지 제거 (요청 쓰레드마다 1개 ~ 10개)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterUserSearchSaveRollback(final UserSearchSaveRollbackEvent userSearchSaveRollbackEvent) {
        Long userId = userSearchSaveRollbackEvent.getUserId();
        userSearchRepository.deleteById(userId);
    }

}
