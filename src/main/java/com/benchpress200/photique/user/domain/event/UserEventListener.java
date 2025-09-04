package com.benchpress200.photique.user.domain.event;

import com.benchpress200.photique.user.domain.entity.UserSearch;
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
     * MySQL commit 시점에 회원가입 트랜잭션이 롤백되면 ES에 저장된 유저 검색 데이터 삭제 처리 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterUserSearchSaveRollback(final UserSearchSaveRollbackEvent userSearchSaveRollbackEvent) {
        // 여기서 ES 저장할 때 예외가 발생하면 전역 예외 처리 핸들러에서 로그 남기고 500 응답
        Long userId = userSearchSaveRollbackEvent.getUserId();
        userSearchRepository.deleteById(userId);
    }

    /**
     * MySQL commit 시점에 유저 업데이트 트랜잭션이 롤백되면 ES에 업데이트된 유저 검색 데이터 롤백 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterUserSearchUpdateRollback(final UserSearchUpdateRollbackEvent userSearchUpdateRollbackEvent) {
        // 여기서 ES 저장할 때 예외가 발생하면 전역 예외 처리 핸들러에서 로그 남기고 500 응답
        UserSearch oldUserSearch = userSearchUpdateRollbackEvent.getOldUserSearch();
        userSearchRepository.save(oldUserSearch);
    }

    /**
     * MySQL commit 시점에 회원탈퇴 트랜잭션이 롤백되면 ES에 삭제된 유저 검색 데이터 롤백 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterUserSearchDeleteRollback(final UserSearchDeleteRollbackEvent userSearchDeleteRollbackEvent) {
        // 여기서 ES 저장할 때 예외가 발생하면 전역 예외 처리 핸들러에서 로그 남기고 500 응답
        UserSearch userSearch = userSearchDeleteRollbackEvent.getUserSearch();
        userSearchRepository.save(userSearch);
    }
}
