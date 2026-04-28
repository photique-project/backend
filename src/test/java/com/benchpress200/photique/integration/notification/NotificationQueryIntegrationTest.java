package com.benchpress200.photique.integration.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("알림 쿼리 API 통합 테스트")
public class NotificationQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private NotificationCommandPort notificationCommandPort;

    @MockitoSpyBean
    private NotificationQueryPort notificationQueryPort;

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
    @DisplayName("알림 페이지 조회")
    class GetNotificationPageTest {

        @Test
        @DisplayName("요청이 유효하면 알림 페이지를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Notification savedNotification = notificationCommandPort.save(
                    NotificationFixture.builder()
                            .receiver(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetNotificationPageAuthenticated(0, 30);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(30))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.isFirst").value(true))
                    .andExpect(jsonPath("$.data.isLast").value(true))
                    .andExpect(jsonPath("$.data.hasNext").value(false))
                    .andExpect(jsonPath("$.data.hasPrevious").value(false))
                    .andExpect(jsonPath("$.data.notifications.length()").value(1))
                    .andExpect(jsonPath("$.data.notifications[0].id").value(savedNotification.getId()))
                    .andExpect(jsonPath("$.data.notifications[0].type").value(savedNotification.getType().getValue()))
                    .andExpect(jsonPath("$.data.notifications[0].isRead").value(false))
                    .andExpect(jsonPath("$.data.unread").value(true));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestGetNotificationPage(0, 30);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // when
            ResultActions resultActions = requestGetNotificationPageAuthenticated(-1, 30);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // when
            ResultActions resultActions = requestGetNotificationPageAuthenticated(0, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("알림 조회 중 DB 예외가 발생하면 500을 반환한다")
        public void whenQueryFails() throws Exception {
            // given
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(notificationQueryPort).findByReceiverIdAndDeletedAtIsNull(any(), any());

            // when
            ResultActions resultActions = requestGetNotificationPageAuthenticated(0, 30);

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private ResultActions requestGetNotificationPage(
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.NOTIFICATION_ROOT);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestGetNotificationPageAuthenticated(
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.NOTIFICATION_ROOT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }
}
