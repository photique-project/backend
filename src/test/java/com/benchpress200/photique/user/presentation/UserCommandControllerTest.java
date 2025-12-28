package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.auth.domain.result.AuthenticationUserResult;
import com.benchpress200.photique.common.constant.MultipartKey;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.command.service.UserCommandService;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.util.DummyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@DisplayName("UserCommandController 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Security Filter 비활성화
@Import(TestContainerConfiguration.class)
public class UserCommandControllerTest {
    private static final String JSON_KEY_EMAIL = "email";
    private static final String JSON_KEY_PASSWORD = "password";
    private static final String JSON_KEY_NICKNAME = "nickname";
    private static final String JSON_KEY_INTRODUCTION = "introduction";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserCommandService userCommandService;


    @BeforeEach
    void setUp() {
        // 컨트롤러 단위 테스트이므로 UserCommandService는 항상 정상 동작하도록 설정
        Mockito.doNothing().when(userCommandService).join(Mockito.any());
        Mockito.doNothing().when(userCommandService).updateUserDetails(Mockito.any());
        Mockito.doNothing().when(userCommandService).updateUserPassword(Mockito.any());
        Mockito.doNothing().when(userCommandService).resetUserPassword(Mockito.any());
        Mockito.doNothing().when(userCommandService).withdraw(Mockito.any());
    }


    @Test
    @DisplayName("join 성공 테스트")
    void join_성공_테스트() throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, password,
                JSON_KEY_NICKNAME, nickname
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));
        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidEmails")
    @DisplayName("join 실패 테스트 - 유효하지 않은 이메일")
    void join_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, invalidEmail,
                JSON_KEY_PASSWORD, password,
                JSON_KEY_NICKNAME, nickname
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));
        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidPasswords")
    @DisplayName("join 실패 테스트 - 유효하지 않은 비밀번호")
    void join_실패_테스트_유효하지_않은_비밀번호(final String invalidPassword) throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String nickname = DummyGenerator.generateNickname();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, invalidPassword,
                JSON_KEY_NICKNAME, nickname
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));
        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidNicknames")
    @DisplayName("join 실패 테스트 - 유효하지 않은 닉네임")
    void join_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, password,
                JSON_KEY_NICKNAME, invalidNickname
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));
        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidProfileImages")
    @DisplayName("join 실패 테스트 - 유효하지 않은 프로필 이미지")
    void join_실패_테스트_유효하지_않은_프로필_이미지(final MockMultipartFile profileImage) throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, password,
                JSON_KEY_NICKNAME, nickname
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("updateUserDetails 성공 테스트")
    void updateUserDetails_성공_테스트() throws Exception {
        // GIVEN
        Long userId = 1L;

        createMockAuthentication(userId);

        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_NICKNAME, nicknameToUpdate,
                JSON_KEY_INTRODUCTION, introductionToUpdate
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 경로 변수")
    void updateUserDetails_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();
        String invalidPathVariable = DummyGenerator.generateInvalidPathVariable();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_NICKNAME, nicknameToUpdate,
                JSON_KEY_INTRODUCTION, introductionToUpdate
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, invalidPathVariable)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidNicknames")
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 닉네임")
    void updateUserDetails_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();
        String introductionToUpdate = DummyGenerator.generateIntroduction();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_NICKNAME, invalidNickname,
                JSON_KEY_INTRODUCTION, introductionToUpdate
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 소개")
    void updateUserDetails_실패_테스트_유효하지_않은_소개() throws Exception {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();
        String nicknameToUpdate = DummyGenerator.generateNickname();
        String invalidIntroduction = DummyGenerator.generateInvalidIntroduction();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_NICKNAME, nicknameToUpdate,
                JSON_KEY_INTRODUCTION, invalidIntroduction
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        MockMultipartFile profileImage = DummyGenerator.generateMockProfileImage(MultipartKey.PROFILE_IMAGE);

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidProfileImagesWhenUpdate")
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 프로필 이미지")
    void updateUserDetails_실패_테스트_유효하지_않은_프로필_이미지(final MockMultipartFile profileImage) throws Exception {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();
        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();

        createMockAuthentication(userId);

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_NICKNAME, nicknameToUpdate,
                JSON_KEY_INTRODUCTION, introductionToUpdate
        );

        MockMultipartFile userPart = DummyGenerator.generateMockUserJson(MultipartKey.USER,
                objectMapper.writeValueAsString(jsonBody));

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }


    @Test
    @DisplayName("updateUserPassword 성공 테스트")
    void updateUserPassword_성공_테스트() throws Exception {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();
        String passwordToUpdate = DummyGenerator.generatePassword();

        createMockAuthentication(userId);

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_PASSWORD, passwordToUpdate
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.PASSWORD, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateUserPassword 실패 테스트 - 유효하지 않은 경로 변수")
    void updateUserPassword_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String invalidPathVariable = DummyGenerator.generateInvalidPathVariable();
        String passwordToUpdate = DummyGenerator.generatePassword();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_PASSWORD, passwordToUpdate
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.PASSWORD, invalidPathVariable)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        Mockito.doNothing().when(userCommandService).updateUserPassword(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidPasswords")
    @DisplayName("updateUserPassword 실패 테스트 - 유효하지 않은 비밀번호")
    void updateUserPassword_실패_테스트_유효하지_않은_비밀번호(final String invalidPassword) throws Exception {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_PASSWORD, invalidPassword
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA + URL.PASSWORD, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("resetUserPassword 성공 테스트")
    void resetUserPassword_성공_테스트() throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String passwordToReset = DummyGenerator.generatePassword();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, passwordToReset
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidEmails")
    @DisplayName("resetUserPassword 실패 테스트 - 유효하지 않은 이메일")
    void resetUserPassword_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        String passwordToReset = DummyGenerator.generatePassword();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, invalidEmail,
                JSON_KEY_PASSWORD, passwordToReset
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidPasswords")
    @DisplayName("resetUserPassword 실패 테스트 - 유효하지 않은 비밀번호")
    void resetUserPassword_실패_테스트_유효하지_않은_비밀번호(final String invalidPassword) throws Exception {
        // GIVEN
        String email = DummyGenerator.generateEmail();

        Map<String, Object> jsonBody = Map.of(
                JSON_KEY_EMAIL, email,
                JSON_KEY_PASSWORD, invalidPassword
        );

        RequestBuilder request = MockMvcRequestBuilders
                .patch(URL.BASE_URL + URL.USER_DOMAIN + URL.PASSWORD)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jsonBody));

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("withdraw 성공 테스트")
    void withdraw_성공_테스트() throws Exception {
        // GIVEN
        Long userId = DummyGenerator.generatePathVariable();
        createMockAuthentication(userId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("withdraw 실패 테스트 - 유효하지 않은 경로 변수")
    void withdraw_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String invalidPathVariable = DummyGenerator.generateInvalidPathVariable();

        RequestBuilder request = MockMvcRequestBuilders
                .delete(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, invalidPathVariable)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    // @PreAuthorize 검증 통과를 위한 임시 인증 객체 생성
    void createMockAuthentication(final Long userId) {
        AuthenticationUserResult authenticationUserResult = AuthenticationUserResult.builder()
                .userId(userId)
                .role(Role.USER.getValue())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationUserResult, null,
                authenticationUserResult.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
