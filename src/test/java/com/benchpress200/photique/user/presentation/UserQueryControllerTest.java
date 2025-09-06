package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.UserQueryService;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@DisplayName("UserQueryController 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserQueryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserQueryService userQueryService;

    @Test
    @DisplayName("validateNickname 성공 테스트")
    void validateNickname_성공_테스트() throws Exception {
        // GIVEN
        String validNickname = "nickname";
        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.GET,
                        URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME + "?nickname=" + validNickname)
                .accept(MediaType.APPLICATION_JSON);

        boolean result = false;
        ValidateNicknameResult validateNicknameResult = ValidateNicknameResult.of(result);
        Mockito.doReturn(validateNicknameResult).when(userQueryService).validateNickname(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Nickname duplication check completed"))
                .andExpect(jsonPath("$.data.isDuplicated").value(false));
    }

    @ParameterizedTest
    @MethodSource("getInvalidNicknames")
    @DisplayName("validateNickname 실패 테스트 - 유효하지 않은 닉네임")
    void validateNickname_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.GET,
                        URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME + "?nickname=" + invalidNickname)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid nickname"));
    }

    // 유효하지 않은 닉네임
    static Stream<String> getInvalidNicknames() {
        return Stream.of(
                "",     // 빈 문자열
                " ",             // 공백
                "abcdefghijkl",  // 12자 초과
                "nick name"      // 공백 포함
        );
    }
}
