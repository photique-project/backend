package com.benchpress200.photique.integration.notification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.notification.application.command.port.out.persistence.NotificationCommandPort;
import com.benchpress200.photique.notification.application.query.port.out.persistence.NotificationQueryPort;
import com.benchpress200.photique.notification.domain.entity.Notification;
import com.benchpress200.photique.notification.domain.support.NotificationFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("알림 커맨드 API 통합 테스트")
public class NotificationCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NotificationCommandPort notificationCommandPort;

    @Autowired
    private NotificationQueryPort notificationQueryPort;

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        notificationCommandPort.deleteAll();
        userCommandPort.deleteAll();

        User user = UserFixture.builder().build();
        savedUser = userCommandPort.save(user);

        AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                savedUser.getId(),
                savedUser.getRole().name()
        );
        accessToken = tokens.getAccessToken();
    }

    @Nested
    @DisplayName("알림 읽음 처리")
    class MarkAsReadTest {

        @Test
        @DisplayName("요청이 유효하면 알림을 읽음 처리하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Notification savedNotification = notificationCommandPort.save(
                    NotificationFixture.builder()
                            .receiver(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestMarkAsReadAuthenticated(savedNotification.getId());
            Optional<Notification> notification = notificationQueryPort.findByIdAndDeletedAtIsNull(
                    savedNotification.getId()
            );

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(notification)
                    .isPresent()
                    .get()
                    .satisfies(n -> Assertions.assertThat(n.isRead()).isTrue());
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            Notification savedNotification = notificationCommandPort.save(
                    NotificationFixture.builder()
                            .receiver(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestMarkAsRead(savedNotification.getId());
            Optional<Notification> notification = notificationQueryPort.findByIdAndDeletedAtIsNull(
                    savedNotification.getId()
            );

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(notification)
                    .isPresent()
                    .get()
                    .satisfies(n -> Assertions.assertThat(n.isRead()).isFalse());
        }

        @Test
        @DisplayName("존재하지 않는 알림이면 404를 반환한다")
        public void whenNotificationNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestMarkAsReadAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    private ResultActions requestMarkAsRead(Long notificationId) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.NOTIFICATION_DATA, notificationId)
        );
    }

    private ResultActions requestMarkAsReadAuthenticated(Long notificationId) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.NOTIFICATION_DATA, notificationId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
