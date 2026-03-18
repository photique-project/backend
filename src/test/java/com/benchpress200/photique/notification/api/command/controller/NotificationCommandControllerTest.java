package com.benchpress200.photique.notification.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.notification.application.command.port.in.DeleteNotificationUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAllAsReadUseCase;
import com.benchpress200.photique.notification.application.command.port.in.MarkAsReadUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = NotificationCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("알림 커맨드 컨트롤러 테스트")
public class NotificationCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private MarkAsReadUseCase markAsReadUseCase;

    @MockitoBean
    private MarkAllAsReadUseCase markAllAsReadUseCase;

    @MockitoBean
    private DeleteNotificationUseCase deleteNotificationUseCase;

    @Test
    @DisplayName("알림 읽음 표시 요청 시 요청이 유효하면 204를 반환한다")
    public void markAsRead_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(markAsReadUseCase).markAsRead(any());

        // when
        ResultActions resultActions = requestMarkAsRead("1");

        // then
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("알림 읽음 표시 요청 시 알림 ID가 숫자가 아니면 400을 반환한다")
    public void markAsRead_whenNotificationIdIsNotNumber() throws Exception {
        // given
        doNothing().when(markAsReadUseCase).markAsRead(any());

        // when
        ResultActions resultActions = requestMarkAsRead("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestMarkAsRead(String notificationId) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.NOTIFICATION_DATA, notificationId)
        );
    }
}
