package com.benchpress200.photique.notification.application.query;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationUserProviderPort;
import com.benchpress200.photique.notification.application.query.model.NotificationPageQuery;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import com.benchpress200.photique.notification.application.query.service.NotificationQueryService;
import com.benchpress200.photique.notification.application.query.support.fixture.NotificationPageQueryFixture;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.support.base.BaseServiceTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("알림 쿼리 서비스 테스트")
public class NotificationQueryServiceTest extends BaseServiceTest {
    @InjectMocks
    private NotificationQueryService notificationQueryService;

    @Mock
    private AuthenticationUserProviderPort authenticationUserProviderPort;

    @Mock
    private NotificationQueryPort notificationQueryPort;

    @Nested
    @DisplayName("알림 페이지 조회")
    class GetNotificationPageTest {
        @Test
        @DisplayName("처리에 성공한다")
        public void whenQueryValid() {
            // given
            NotificationPageQuery query = NotificationPageQueryFixture.builder().build();
            Page<Notification> notificationPage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(notificationPage).when(notificationQueryPort).findByReceiverIdAndDeletedAtIsNull(any(), any());
            doReturn(false).when(notificationQueryPort).existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(any());

            // when
            NotificationPageResult result = notificationQueryService.getNotificationPage(query);

            // then
            verify(notificationQueryPort).findByReceiverIdAndDeletedAtIsNull(1L, query.getPageable());
            verify(notificationQueryPort).existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(1L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("알림 페이지 조회에 실패하면 예외를 던진다")
        public void whenFindPageFails() {
            // given
            NotificationPageQuery query = NotificationPageQueryFixture.builder().build();

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doThrow(new RuntimeException()).when(notificationQueryPort).findByReceiverIdAndDeletedAtIsNull(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> notificationQueryService.getNotificationPage(query)
            );
            verify(notificationQueryPort, never()).existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(any());
        }

        @Test
        @DisplayName("읽지 않은 알림 존재 여부 조회에 실패하면 예외를 던진다")
        public void whenExistsByUnreadFails() {
            // given
            NotificationPageQuery query = NotificationPageQueryFixture.builder().build();
            Page<Notification> notificationPage = new PageImpl<>(List.of(), PageRequest.of(0, 30), 0);

            doReturn(1L).when(authenticationUserProviderPort).getCurrentUserId();
            doReturn(notificationPage).when(notificationQueryPort).findByReceiverIdAndDeletedAtIsNull(any(), any());
            doThrow(new RuntimeException()).when(notificationQueryPort).existsByReceiverIdAndIsReadFalseAndDeletedAtIsNull(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> notificationQueryService.getNotificationPage(query)
            );
        }
    }
}
