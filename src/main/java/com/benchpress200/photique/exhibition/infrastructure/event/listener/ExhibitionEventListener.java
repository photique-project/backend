package com.benchpress200.photique.exhibition.infrastructure.event.listener;

import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.ExhibitionTagQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionSearch;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionTag;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionCreateEvent;
import com.benchpress200.photique.exhibition.domain.event.ExhibitionWorkImageUploadEvent;
import com.benchpress200.photique.exhibition.domain.exception.ExhibitionNotFoundException;
import com.benchpress200.photique.exhibition.infrastructure.persistence.elasticsearch.ExhibitionSearchRepository;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.NotificationType;
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
public class ExhibitionEventListener {
    private static final int BATCH_SIZE = 100;
    private static final int START_PAGE_NUMBER = 0;
    private static final String DEFAULT_SORT_COLUMN = "createdAt";

    private final EntityManager entityManager;

    private final FollowQueryPort followQueryPort;
    private final NotificationCommandPort notificationCommandPort;
    private final ImageUploaderPort imageUploaderPort;
    private final ExhibitionQueryPort exhibitionQueryPort;
    private final ExhibitionTagQueryPort exhibitionTagQueryPort;
    private final ExhibitionSearchRepository exhibitionSearchRepository;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleExhibitionWorkImageUploadEventIfRollback(ExhibitionWorkImageUploadEvent event) {
        String imageUrl = event.getImageUrl();
        imageUploaderPort.delete(imageUrl);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleExhibitionCreateEventIfCommit(ExhibitionCreateEvent event) {
        // FIXME: 이후 메시지 큐 도입한다면 메시지 발행해서 MySQL - ES 동기화 컨슈머 추가하여 비동기 처리
        Long exhibitionId = event.getExhibitionId();
        Exhibition exhibition = exhibitionQueryPort.findActiveByIdWithWriter(exhibitionId)
                .orElseThrow(() -> new ExhibitionNotFoundException(exhibitionId));

        // 태그 조회
        List<ExhibitionTag> exhibitionTags = exhibitionTagQueryPort.findByExhibitionWithTag(exhibition);
        List<String> exhibitionTagNames = exhibitionTags.stream()
                .map(exhibitionTag -> exhibitionTag.getTag().getName())
                .toList();

        // 전시회 검색 엔티티 생성 및 저장
        ExhibitionSearch exhibitionSearch = ExhibitionSearch.of(
                exhibition,
                exhibitionTagNames
        );

        exhibitionSearchRepository.save(exhibitionSearch);

        // FIXME: 메시지 큐 도입하면 위 ES 동기화 & 아래 팔로워 알림 생성에 대한 비동기 처리 프로세스 추가 작성 필요
        User writer = exhibition.getWriter();

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
                        NotificationType.FOLLOWING_SINGLE_WORK,
                        exhibitionId
                );

                buffer.add(notification);
            }

            notificationCommandPort.saveAll(buffer);
            entityManager.flush();
            entityManager.clear();

            pageable = slice.nextPageable();
        } while (slice.hasNext());
    }
}
