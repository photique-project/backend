package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.FollowCommandService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@DisplayName("FollowCommandControllerTest 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Security Filter 비활성화
@EnableMethodSecurity(prePostEnabled = false)
public class FollowCommandControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoSpyBean
    FollowCommandService followCommandService;

    @BeforeEach
    void setUp() {
        // 컨트롤러 단위 테스트이므로 FollowCommandControllerTest 항상 정상 동작하도록 설정
        Mockito.doNothing().when(followCommandService).follow(Mockito.any());
        Mockito.doNothing().when(followCommandService).unfollow(Mockito.any());
    }

    @Test
    @DisplayName("follow 성공 테스트")
    void follow_성공_테스트() throws Exception {
        // GIVEN
        String userId = "/1";

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.USER_DOMAIN + userId + URL.FOLLOW_DOMAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    @DisplayName("follow 실패 테스트 - 유효하지 않은 경로 변수")
    void follow_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String invalidUserId = "/a";

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.USER_DOMAIN + invalidUserId + URL.FOLLOW_DOMAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("unfollow 성공 테스트")
    void unfollow_성공_테스트() throws Exception {
        // GIVEN
        String userId = "/1";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(URL.BASE_URL + URL.USER_DOMAIN + userId + URL.FOLLOW_DOMAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("unfollow 실패 테스트 - 유효하지 않은 경로 변수")
    void unfollow_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String invalidUserId = "/a";

        RequestBuilder request = MockMvcRequestBuilders
                .delete(URL.BASE_URL + URL.USER_DOMAIN + invalidUserId + URL.FOLLOW_DOMAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
