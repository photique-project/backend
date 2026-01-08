package com.benchpress200.photique.singlework.infrastructure.event.listener;

import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkTagQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkSearch;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkTag;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCommentCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkCreateEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkDeleteEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkImageUploadEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkLikeAddEvent;
import com.benchpress200.photique.singlework.domain.event.SingleWorkUpdateEvent;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkNotFoundException;
import com.benchpress200.photique.singlework.infrastructure.persistence.elasticsearch.SingleWorkSearchRepository;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
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
public class SingleWorkEventListener {
    private static final int BATCH_SIZE = 100;
    private static final int START_PAGE_NUMBER = 0;
    private static final String DEFAULT_SORT_COLUMN = "createdAt";

    private final EntityManager entityManager;

    private final ImageUploaderPort imageUploaderPort;
    private final SingleWorkQueryPort singleWorkQueryPort;
    private final SingleWorkTagQueryPort singleWorkTagQueryPort;
    private final SingleWorkLikeQueryPort singleWorkLikeQueryPort;
    private final FollowQueryPort followQueryPort;
    private final NotificationCommandPort notificationCommandPort;

    private final SingleWorkSearchRepository singleWorkSearchRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleSingleWorkImageUploadEventIfRollback(SingleWorkImageUploadEvent event) {
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkCreateEventIfCommit(SingleWorkCreateEvent event) {
        // FIXME: 이후 메시지 큐 도입한다면 메시지 발행해서 MySQL - ES 동기화 컨슈머 추가하여 비동기 처리

        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 태그 조회
        List<SingleWorkTag> singleWorkTags = singleWorkTagQueryPort.findBySingleWorkWithTag(singleWork);
        List<String> singleWorkTagNames = singleWorkTags.stream()
                .map(singleWorkTag -> singleWorkTag.getTag().getName())
                .toList();

        // 단일작품 검색 엔티티 생성 및 저장
        SingleWorkSearch singleWorkSearch = SingleWorkSearch.of(
                singleWork,
                singleWorkTagNames
        );

        singleWorkSearchRepository.save(singleWorkSearch);

        // FIXME: 메시지 큐 도입하면 위 ES 동기화 & 아래 팔로워 알림 생성에 대한 비동기 처리 프로세스 추가 작성 필요
        User writer = singleWork.getWriter();

        // 페이지네이션할 때, ORDER BY가 없다면 실행계획에 따라 정렬이 깨져서 페이징이 이상할 수 있으므로 정렲필요
        Pageable pageable = PageRequest.of(
                START_PAGE_NUMBER,
                BATCH_SIZE,
                Sort.by(DEFAULT_SORT_COLUMN)
        );

        Slice<Follow> slice;

        do {
            slice = followQueryPort.findByFolloweeWithFollower(writer, pageable);
            List<Notification> buffer = new ArrayList<>(BATCH_SIZE);
            List<Follow> follows = slice.getContent();

            for (Follow follow : follows) {
                User follower = follow.getFollower();

                Notification notification = Notification.of(
                        follower,
                        NotificationType.FOLLOWING_SINGLEWORK,
                        singleWorkId
                );

                buffer.add(notification);
            }

            notificationCommandPort.saveAll(buffer);
            entityManager.flush();
            entityManager.clear();

            pageable = slice.nextPageable();
        } while (slice.hasNext());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkUpdateEventIfCommit(SingleWorkUpdateEvent event) {
        // FIXME: 이후 메시지 큐 도입한다면 메시지 발행해서 MySQL - ES 동기화 컨슈머 추가하여 비동기 처리

        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        // 태그 조회
        List<SingleWorkTag> singleWorkTags = singleWorkTagQueryPort.findBySingleWorkWithTag(singleWork);
        List<String> singleWorkTagNames = singleWorkTags.stream()
                .map(singleWorkTag -> singleWorkTag.getTag().getName())
                .toList();

        // 단일작품 검색 엔티티 생성 후 덮어쓰기
        SingleWorkSearch singleWorkSearch = SingleWorkSearch.of(
                singleWork,
                singleWorkTagNames
        );

        singleWorkSearchRepository.save(singleWorkSearch);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkDeleteEventIfCommit(SingleWorkDeleteEvent event) {
        Long singleWorkId = event.getSingleWorkId();
        singleWorkSearchRepository.deleteById(singleWorkId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkLikeAddEventIfCommit(SingleWorkLikeAddEvent event) {
        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        User receiver = singleWork.getWriter();

        Notification notification = Notification.of(
                receiver,
                NotificationType.SINGLEWORK_LIKE,
                singleWorkId
        );

        notificationCommandPort.save(notification);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSingleWorkCommentCreateEventIfCommit(SingleWorkCommentCreateEvent event) {
        Long singleWorkId = event.getSingleWorkId();
        SingleWork singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(singleWorkId)
                .orElseThrow(() -> new SingleWorkNotFoundException(singleWorkId));

        User receiver = singleWork.getWriter();

        Notification notification = Notification.of(
                receiver,
                NotificationType.SINGLEWORK_COMMENT,
                singleWorkId
        );

        notificationCommandPort.save(notification);
    }
}
