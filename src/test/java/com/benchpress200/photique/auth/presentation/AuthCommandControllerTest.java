package com.benchpress200.photique.auth.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
import com.benchpress200.photique.auth.application.command.service.AuthCommandService;
import com.benchpress200.photique.auth.domain.port.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.util.DummyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@DisplayName("AuthCommandController 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(TestContainerConfiguration.class)
public class AuthCommandControllerTest {
    private static final String JSON_KEY_EMAIL = "email";
    private static final String JSON_KEY_CODE = "code";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final int AUTH_MAIL_CODE_LENGTH = 6;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoBean
    AuthCommandService authCommandService;

    @Test
    @DisplayName("sendJoinAuthMail 성공 테스트")
    void sendJoinAuthMail_성공_테스트() throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        Map<String, Object> jsonBody = Map.of(JSON_KEY_EMAIL, email);
        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.JOIN_MAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        Mockito
                .doNothing()
                .when(authCommandService)
                .sendJoinAuthMail(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidEmails")
    @DisplayName("sendJoinAuthMail 실패 테스트 - 유효하지 않은 이메일")
    void sendJoinAuthMail_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        Map<String, Object> jsonBody = Map.of(JSON_KEY_EMAIL, invalidEmail);
        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.JOIN_MAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("sendPasswordAuthMail - 성공 테스트")
    void sendPasswordAuthMail_성공_테스트() throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        Map<String, Object> jsonBody = Map.of(JSON_KEY_EMAIL, email);
        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.PASSWORD_MAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        Mockito
                .doNothing()
                .when(authCommandService)
                .sendPasswordAuthMail(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidEmails")
    @DisplayName("sendPasswordAuthMail 실패 테스트 - 유효하지 않은 이메일")
    void sendPasswordAuthMail_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        Map<String, Object> jsonBody = Map.of(JSON_KEY_EMAIL, invalidEmail);
        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.PASSWORD_MAIL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("validateAuthMailCode 성공 테스트")
    void validateAuthMailCode_성공_테스트() throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String code = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_CODE, code
        );

        boolean success = true;
        AuthMailCodeValidateResult authMailCodeValidationResult = AuthMailCodeValidateResult.of(success);

        Mockito
                .when(authCommandService.validateAuthMailCode(Mockito.any()))
                .thenReturn(authMailCodeValidationResult);

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.VALIDATE_CODE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidEmails")
    @DisplayName("validateAuthMailCode 실패 테스트 - 유효하지 않은 이메일")
    void validateAuthMailCode_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        String code = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, invalidEmail,
                JSON_KEY_CODE, code
        );

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.VALIDATE_CODE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidAuthMailCodes")
    @DisplayName("validateAuthMailCode 실패 테스트 - 유효하지 않은 인증 코드")
    void validateAuthMailCode_실패_테스트_유효하지_않은_인증_코드(final String invalidCode) throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_CODE, invalidCode
        );

        boolean success = true;
        AuthMailCodeValidateResult authMailCodeValidationResult = AuthMailCodeValidateResult.of(success);

        Mockito
                .when(authCommandService.validateAuthMailCode(Mockito.any()))
                .thenReturn(authMailCodeValidationResult);

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.VALIDATE_CODE)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("refreshAuthToken 성공 테스트")
    void refreshAuthToken_성공_테스트() throws Exception {
        // GIVEN
        Long userId = DummyGenerator.generateResourceId();
        String role = DummyGenerator.generateRole();
        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);
        String refreshToken = authenticationTokens.getRefreshToken();
        AuthTokenResult authTokenResult = AuthTokenResult.from(authenticationTokens);
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);

        Mockito
                .doReturn(authTokenResult)
                .when(authCommandService)
                .refreshAuthToken(Mockito.any(AuthTokenRefreshCommand.class));

        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.REFRESH_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("refreshAuthToken 실패 테스트 - 존재하지 않는 리프레쉬 토큰")
    void refreshAuthToken_실패_테스트_존재하지_않는_리프레쉬_토큰() throws Exception {
        // GIVEN
        RequestBuilder request = MockMvcRequestBuilders
                .post(URL.BASE_URL + URL.AUTH_DOMAIN + URL.REFRESH_TOKEN)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
