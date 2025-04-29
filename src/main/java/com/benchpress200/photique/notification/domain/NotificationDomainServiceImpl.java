package com.benchpress200.photique.notification.domain;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.exception.NotificationException;
import com.benchpress200.photique.notification.infrastructure.NotificationRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.ServletEndpointManagementContextConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationDomainServiceImpl implements NotificationDomainService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;
    private final ServletEndpointManagementContextConfiguration servletEndpointManagementContextConfiguration;

    @Override
    public SseEmitter subscribe(Long userId) {
        if (emitters.containsKey(userId)) {
            emitters.get(userId).complete();
        }

        // emitter추가
        SseEmitter sseEmitter = new SseEmitter(60L * 1000 * 10);
        emitters.put(userId, sseEmitter);

        // 종료 되었을 때 처리
        sseEmitter.onCompletion(() -> {
            sseEmitter.complete();
            emitters.remove(userId);
        });

        // time out 시 처리
        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
            emitters.remove(userId);
        });

        // 에러 처리
        sseEmitter.onError((throwable) -> {
            sseEmitter.complete();
            emitters.remove(userId);
        });

        try {
            sseEmitter.send(
                    SseEmitter.event().id(String.valueOf(userId))
                            .data("SSE server connect completed!"));
        } catch (IOException e) {
            System.out.println("SSE IOException");
            sseEmitter.complete();
            emitters.remove(userId);
        }

        return sseEmitter;
    }

    @Async
    @Override
    public void createNotification(final Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public Page<Notification> findNotifications(
            final User user,
            final Pageable pageable
    ) {
        Page<Notification> notificationPage = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        if (notificationPage.getTotalElements() == 0) {
            throw new NotificationException("No notifications found.", HttpStatus.NOT_FOUND);
        }
        return notificationPage;
    }

    @Override
    public Notification findNotification(Long notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotificationException("Notification with id " + notificationId + " is not found.",
                        HttpStatus.NOT_FOUND)
        );
    }

    @Override
    public void readNotification(final Notification notification) {
        notification.read();
    }

    @Override
    public List<Notification> findNotifications(final User user) {
        return notificationRepository.findByUser(user);
    }

    @Override
    public void deleteNotification(final Notification notification) {
        notificationRepository.delete(notification);
    }

    @Async
    public void pushNewNotification(final Long userId) {

        // 로그인상태가 아닌 유저라면 실시간 알림 전송 X
        if (!emitters.containsKey(userId)) {
            return;
        }

        // 노티 엔티티 전송이 아닌 일반 알림을 위한 데이터 전송
        try {
            SseEmitter emitter = emitters.get(userId);
            emitter.send(SseEmitter.event().id(String.valueOf(userId)).data("new"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long countUnread(final User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
