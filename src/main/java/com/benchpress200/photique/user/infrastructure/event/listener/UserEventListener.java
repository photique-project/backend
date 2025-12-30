package com.benchpress200.photique.user.infrastructure.event.listener;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch.SingleWorkSearchRepository;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.event.UserDetailsUpdateEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageDeleteEvent;
import com.benchpress200.photique.user.domain.event.UserProfileImageUploadEvent;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {
    private static final int BATCH_SIZE = 100;
    private static final int START_PAGE_NUMBER = 0;
    private static final String DEFAULT_SORT_COLUMN = "createdAt";

    private final ImageUploaderPort imageUploaderPort;
    private final UserQueryPort userQueryPort;
    private final SingleWorkSearchRepository singleWorkSearchRepository;


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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdateUserDetailsEventIfCommit(UserDetailsUpdateEvent event) {
        // FIXME: 이후 메시지 큐 도입한다면 배치 처리는 컨슈머에서 수행
        Long userId = event.getUserId();
        User user = userQueryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Pageable pageable = PageRequest.of(
                START_PAGE_NUMBER,
                BATCH_SIZE,
                Sort.by(DEFAULT_SORT_COLUMN)
        );

        Page<SingleWorkSearch> page;

        do {
            page = singleWorkSearchRepository.findByWriterId(userId, pageable);
            List<SingleWorkSearch> singleWorkSearches = page.getContent();

            for (SingleWorkSearch singleWorkSearch : singleWorkSearches) {
                singleWorkSearch.updateWriterDetails(user);
            }

            singleWorkSearchRepository.saveAll(singleWorkSearches);
            pageable = page.nextPageable();
        } while (page.hasNext());
    }
}
