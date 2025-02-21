package com.benchpress200.photique.notification.domain;

import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.enumeration.Type;
import com.benchpress200.photique.notification.exception.NotificationException;
import com.benchpress200.photique.notification.infrastructure.NotificationRepository;
import com.benchpress200.photique.user.domain.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationDomainServiceImpl implements NotificationDomainService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NotificationRepository notificationRepository;

    @Override
    public SseEmitter subscribe(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(60L * 1000 * 10);
        // emitter추가
        emitters.put(userId, sseEmitter);

        // 종료 되었을 때 처리
        sseEmitter.onCompletion(() -> {
            System.out.println("연결종료처리");
            emitters.remove(userId);
        });

        // time out 시 처리
        sseEmitter.onTimeout(() -> {
            System.out.println("타임아웃");
            emitters.remove(userId);
        });

        try {
            sseEmitter.send(
                    SseEmitter.event().id(String.valueOf(userId))
                            .data("SSE server connect completed!!"));
        } catch (IOException e) {
            System.out.println("IOException");
        }

        return sseEmitter;
    }

    @Override
    public void pushNewNotification(
            final User user,
            final Type type,
            final Long targetId
    ) {

        // 새 노티 만들기
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .targetId(targetId)
                .build();

        // 데이터베이스 저장
        notification = notificationRepository.save(notification);

        // 접속중인 유저에게 알림 전송
        send(user, notification);
    }

    @Override
    public Page<Notification> findNotifications(
            final User user,
            final Pageable pageable
    ) {
        Page<Notification> notificationPage = notificationRepository.findByUser(user, pageable);
        if (notificationPage.getTotalElements() == 0) {
            throw new NotificationException("No notifications found.", HttpStatus.NOT_FOUND);
        }
        return notificationRepository.findByUser(user, pageable);
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

    private void send(final User user, final Notification notification) {

        // 로그인상태가 아닌 유저라면 실시간 알림 전송 X
        if (!emitters.containsKey(user.getId())) {
            return;
        }

        try {
            String notificationId = notification.getId().toString();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());  // LocalDateTime 직렬화 지원
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String jsonData = objectMapper.writeValueAsString(notification);

            SseEmitter emitter = emitters.get(user.getId());
            emitter.send(SseEmitter.event().id(notificationId).data(jsonData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String createContent(
            final User user,
            final Type type
    ) {
//
//        switch (type) {
//            case SINGLE_WORK_COMMENT:
//                return "💬 단일작품에 새로운 댓글이 추가되었습니다.";
//            case EXHIBITION_COMMENT:
//                return "💬 전시회에 새로운 감상평이 추가되었습니다.";
//            case SINGLE_WORK_LIKE:
//            case EXHIBITION_LIKE:
//                return "❤️ 누군가 좋아요를 눌렀어요!";
//            case EXHIBITION_BOOKMARK:
//                return "⭐️ 누군가 전시회를 저장했어요!";
//            case FOLLOWING_SINGLE_WORK:
//                return "🔥" + user.getNickname() + "님이 새 단일작품을 올렸어요.";
//            case FOLLOWING_EXHIBITION:
//                return "🔥" + user.getNickname() + "님이 새 전시회를 열었어요.";
//            case FOLLOW:
//                return "👀" + +"님이 당신을 팔로우했습니다!";
//            default:
//        }

        return null;
    }
}
