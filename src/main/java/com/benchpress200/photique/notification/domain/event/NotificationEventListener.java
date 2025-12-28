package com.benchpress200.photique.notification.domain.event;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.notification.domain.exception.NotificationTargetSingleWorkNotFoundException;
import com.benchpress200.photique.notification.domain.repository.NotificationRepository;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.repository.SingleWorkRepository;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private static final int BATCH_SIZE = 100;
    private static final int START_PAGE_NUMBER = 0;
    private static final String DEFAULT_SORT_COLUMN = "createdAt";

    private final EntityManager entityManager;
    private final SingleWorkRepository singleWorkRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    // TODO: 이후 메시지 큐 도입한다면 메시지 발행 & 실패 재시도 & 컨슈머 비동기 처리 고려
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCreateSingleWorkNotificationEventIfCommit(CreateSingleWorkNotificationEvent event) {
        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkRepository.findWithWriter(singleWorkId)
                .orElseThrow(() -> new NotificationTargetSingleWorkNotFoundException(singleWorkId));

        // 해당 유저를 팔로잉하고 있는 유저들 대상으로 알림생성해야함
        // 팔로워 슬라이스로 조회
        User writer = singleWork.getWriter();

        // 페이지네이션할 때, ORDER BY가 없다면 실행계획에 따라 정렬이 깨져서 페이징이 이상할 수 있으므로 정렲필요
        Pageable pageable = PageRequest.of(
                START_PAGE_NUMBER,
                BATCH_SIZE,
                Sort.by(DEFAULT_SORT_COLUMN)
        );

        Slice<Follow> slice;

        // DB 왕복횟수 확인
        do {
            slice = followRepository.findByFollowee(writer, pageable);
            List<Notification> buffer = new ArrayList<>(BATCH_SIZE);
            List<Follow> follows = slice.getContent();

            for (Follow follow : follows) {
                User follower = follow.getFollower();

                Notification notification = Notification.of(
                        follower,
                        NotificationType.FOLLOWING_SINGLE_WORK,
                        singleWorkId
                );

                buffer.add(notification);
            }

            notificationRepository.saveAll(buffer);
            entityManager.flush();
            entityManager.clear();

            pageable = slice.nextPageable();
        } while (slice.hasNext());
    }
}
