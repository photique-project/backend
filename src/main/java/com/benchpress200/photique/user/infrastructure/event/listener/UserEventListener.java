package com.benchpress200.photique.user.infrastructure.event.listener;

import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkSearchRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.event.UpdateUserDetailsEvent;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
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

    private final UserRepository userRepository;
    private final SingleWorkSearchRepository singleWorkSearchRepository;

    // TODO: 이후 메시지 큐 도입한다면 메시지 발행 & 실패 재시도 & 컨슈머 비동기 처리 고려

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUpdateUserDetailsEventIfCommit(UpdateUserDetailsEvent updateUserDetailsEvent) {
        Long userId = updateUserDetailsEvent.getUserId();
        User user = userRepository.findById(userId)
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
