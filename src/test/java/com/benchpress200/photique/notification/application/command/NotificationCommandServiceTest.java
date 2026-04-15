package com.benchpress200.photique.notification.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.application.command.service.NotificationCommandService;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.exception.NotificationNotFoundException;
import com.benchpress200.photique.notification.domain.support.NotificationFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("알림 커맨드 서비스 테스트")
public class NotificationCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private NotificationCommandService notificationCommandService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private NotificationCommandPort notificationCommandPort;

    @Mock
    private NotificationQueryPort notificationQueryPort;

    @Nested
    @DisplayName("알림 읽음 표시")
    class MarkAsReadTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            Notification notification = NotificationFixture.builder().build();

            doReturn(Optional.of(notification)).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when
            notificationCommandService.markAsRead(1L);

            // then
            verify(notificationQueryPort).findByIdAndDeletedAtIsNull(1L);
        }

        @Test
        @DisplayName("알림이 존재하지 않으면 NotificationNotFoundException을 던진다")
        public void whenNotificationNotFound() {
            // given
            doReturn(Optional.empty()).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    NotificationNotFoundException.class,
                    () -> notificationCommandService.markAsRead(1L)
            );
        }

        @Test
        @DisplayName("알림 조회에 실패하면 예외를 던진다")
        public void whenNotificationQueryFails() {
            // given
            doThrow(new RuntimeException()).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> notificationCommandService.markAsRead(1L)
            );
        }
    }

    @Nested
    @DisplayName("전체 알림 읽음 표시")
    class MarkAllAsReadTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();

            // when
            notificationCommandService.markAllAsRead();

            // then
            verify(authenticationUserProviderPort).getCurrentUserId();
            verify(notificationCommandPort).markAllAsReadByReceiverIdAndDeletedAtIsNull(1L);
        }

        @Test
        @DisplayName("전체 읽음 처리에 실패하면 예외를 던진다")
        public void whenMarkAllAsReadFails() {
            // given
            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(notificationCommandPort).markAllAsReadByReceiverIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> notificationCommandService.markAllAsRead()
            );
        }
    }

    @Nested
    @DisplayName("알림 삭제")
    class DeleteNotificationTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenCommandValid() {
            // given
            Notification notification = NotificationFixture.builder().build();

            doReturn(Optional.of(notification)).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when
            notificationCommandService.deleteNotification(1L);

            // then
            verify(notificationQueryPort).findByIdAndDeletedAtIsNull(1L);
        }

        @Test
        @DisplayName("알림이 존재하지 않으면 아무 처리도 하지 않는다")
        public void whenNotificationNotFound() {
            // given
            doReturn(Optional.empty()).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when
            notificationCommandService.deleteNotification(1L);

            // then
            verify(notificationQueryPort).findByIdAndDeletedAtIsNull(1L);
        }

        @Test
        @DisplayName("알림 조회에 실패하면 예외를 던진다")
        public void whenNotificationQueryFails() {
            // given
            doThrow(new RuntimeException()).when(notificationQueryPort).findByIdAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> notificationCommandService.deleteNotification(1L)
            );
        }
    }
}
