package com.benchpress200.photique.notification.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.notification.application.query.port.in.GetNotificationPageUserCase;
import com.benchpress200.photique.notification.application.query.result.NotificationPageResult;
import com.benchpress200.photique.notification.application.query.support.fixture.NotificationPageResultFixture;
import com.benchpress200.photique.support.base.BaseControllerTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(
        controllers = NotificationQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("알림 쿼리 컨트롤러 테스트")
public class NotificationQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private GetNotificationPageUserCase getNotificationPageUserCase;

    @Nested
    @DisplayName("알림 페이지 조회")
    class GetNotificationPageTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            NotificationPageResult result = NotificationPageResultFixture.builder().build();
            doReturn(result).when(getNotificationPageUserCase).getNotificationPage(any());

            // when
            ResultActions resultActions = requestGetNotificationPage(
                    get(ApiPath.NOTIFICATION_ROOT)
                            .param("page", "0")
                            .param("size", "10")
            );

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("페이지 번호가 유효하지 않으면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            NotificationPageResult result = NotificationPageResultFixture.builder().build();
            doReturn(result).when(getNotificationPageUserCase).getNotificationPage(any());

            // when
            ResultActions resultActions = requestGetNotificationPage(
                    get(ApiPath.NOTIFICATION_ROOT)
                            .param("page", "-1")
                            .param("size", "10")
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.notification.api.query.controller.NotificationQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(String invalidSize) throws Exception {
            // given
            NotificationPageResult result = NotificationPageResultFixture.builder().build();
            doReturn(result).when(getNotificationPageUserCase).getNotificationPage(any());

            // when
            ResultActions resultActions = requestGetNotificationPage(
                    get(ApiPath.NOTIFICATION_ROOT)
                            .param("page", "0")
                            .param("size", invalidSize)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidSizes() {
        return Stream.of(
                "0",    // 최솟값 미만
                "51"    // 최댓값 초과
        );
    }

    private ResultActions requestGetNotificationPage(
            MockHttpServletRequestBuilder requestBuilder
    ) throws Exception {
        return mockMvc.perform(requestBuilder);
    }
}
