package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.UserCommandService;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@DisplayName("UserCommandController 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserCommandControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        // 컨트롤러 단위 테스트이므로 UserCommandService는 항상 정상 동작하도록 설정
        Mockito.doNothing().when(userCommandService).join(Mockito.any());
        Mockito.doNothing().when(userCommandService).updateUserDetails(Mockito.any());
    }


    @Test
    @DisplayName("join 성공 테스트")
    void join_성공_테스트() throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"email\": \"example@google.com\", " +
                        "\"password\": \"password12!@\", " +
                        "\"nickname\": \"nickname\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Join completed"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "email",                 // 단순 문자열
            "missing-at.com",        // @ 없음
            "missing-domain@",       // 도메인 없음
            "user@.com",             // 잘못된 도메인
            "user@domain",           // TLD 없음
            "",                      // 빈 문자열
            " "                      // 공백 문자열
    })
    @DisplayName("join 실패 테스트 - 유효하지 않은 이메일")
    void join_실패_테스트_유효하지_않은_이메일(final String invalidEmail) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"email\": \"" + invalidEmail + "\", " +
                        "\"password\": \"password12!@\", " +
                        "\"nickname\": \"nickname\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid email"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",                // 빈 문자열
            " ",               // 공백
            "12345678",        // 숫자만 있고 문자/특수문자 없음
            "password",        // 문자만 있고 숫자/특수문자 없음
            "password1",       // 문자+숫자만 있고 특수문자 없음
            "!!!!!!!!",        // 특수문자만 있고 숫자/문자 없음
            "pass!@#$",        // 문자+특수문자만 있고 숫자 없음
            "1234!@#$",        // 숫자+특수문자만 있고 문자 없음
            "pa1!",            // 길이가 8 미만 (4자리)
            "PasswordPassword" // 8자 이상이지만 숫자/특수문자 없음
    })
    @DisplayName("join 실패 테스트 - 유효하지 않은 비밀번호")
    void join_실패_테스트_유효하지_않은_비밀번호(final String invalidPassword) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"email\": \"example@google.com\", " +
                        "\"password\": \"" + invalidPassword + "\", " +
                        "\"nickname\": \"nickname\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",              // 빈 문자열
            " ",             // 공백
            "abcdefghijkl",  // 12자 초과
            "nick name",     // 공백 포함
    })
    @DisplayName("join 실패 테스트 - 유효하지 않은 닉네임")
    void join_실패_테스트_유효하지_않은_닉네임(final String nickname) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"email\": \"example@google.com\", " +
                        "\"password\": \"password12!@\", " +
                        "\"nickname\": \"" + nickname + "\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid nickname"));
    }

    @ParameterizedTest
    @MethodSource("getInvalidFiles")
    @DisplayName("join 실패 테스트 - 유효하지 않은 프로필 이미지")
    void join_실패_테스트_유효하지_않은_프로필_이미지(final MockMultipartFile profileImage) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"email\": \"example@google.com\", " +
                        "\"password\": \"password12!@\", " +
                        "\"nickname\": \"nickname\" }").getBytes()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.POST, URL.BASE_URL + URL.USER_DOMAIN)
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid profile image"));
    }

    @Test
    @DisplayName("updateUserDetails 성공 테스트")
    void updateUserDetails_성공_테스트() throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"nickname\": \"updateNick\", " +
                        "\"introduction\": \"introduction\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + "/1")
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("User details updated"));
    }

    @Test
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 경로 변수")
    void updateUserDetails_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"nickname\": \"updateNick\", " +
                        "\"introduction\": \"introduction\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + "/a")
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid path variable type"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",              // 빈 문자열
            " ",             // 공백
            "abcdefghijkl",  // 12자 초과
            "nick name",     // 공백 포함
    })
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 닉네임")
    void updateUserDetails_실패_테스트_유효하지_않은_닉네임(final String nickname) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"nickname\": \"" + nickname + "\", " +
                        "\"introduction\": \"introduction\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + "/1")
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid nickname"));
    }

    @Test
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 소개")
    void updateUserDetails_실패_테스트_유효하지_않은_소개() throws Exception {
        String invalidIntroduction = "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghijz";

        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"nickname\": \"updateNick\", " +
                        "\"introduction\": \"" + invalidIntroduction + "\" }").getBytes()
        );

        MockMultipartFile profileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + "/1")
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid introduction"));
    }

    @ParameterizedTest
    @MethodSource("getInvalidFiles")
    @DisplayName("updateUserDetails 실패 테스트 - 유효하지 않은 프로필 이미지")
    void updateUserDetails_실패_테스트_유효하지_않은_프로필_이미지(final MockMultipartFile profileImage) throws Exception {
        // GIVEN
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "",
                "application/json",
                ("{ \"nickname\": \"updateNick\", " +
                        "\"introduction\": \"introduction\" }").getBytes()
        );

        RequestBuilder request = MockMvcRequestBuilders
                .multipart(HttpMethod.PATCH, URL.BASE_URL + URL.USER_DOMAIN + "/1")
                .file(userPart)
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid profile image"));
    }

    // 유효하지 않은 프로필 이미지 파일
    static Stream<MockMultipartFile> getInvalidFiles() {
        return Stream.of(
                new MockMultipartFile("profileImage", "empty.png", "image/png", new byte[0]), // 빈 파일
                new MockMultipartFile("profileImage", "big.png", "image/png", new byte[5 * 1024 * 1024 + 1]), // 5MB 초과
                new MockMultipartFile("profileImage", "file.txt", "text/plain", "dummy".getBytes()) // 확장자 틀림
        );
    }
}
