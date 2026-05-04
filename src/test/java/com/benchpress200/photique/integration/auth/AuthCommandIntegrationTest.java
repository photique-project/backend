package com.benchpress200.photique.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.api.command.request.AuthMailCodeValidateRequest;
import com.benchpress200.photique.auth.api.command.request.AuthMailRequest;
import com.benchpress200.photique.auth.api.command.support.fixture.AuthMailCodeValidateRequestFixture;
import com.benchpress200.photique.auth.api.command.support.fixture.AuthMailRequestFixture;
import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("인증 커맨드 API 통합 테스트")
public class AuthCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AuthMailCodeQueryPort authMailCodeQueryPort;

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthMailCodeCommandPort authMailCodeCommandPort;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void cleanUp() {
        userCommandPort.deleteAll();
        authMailCodeCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("회원가입 인증 메일 전송")
    class SendJoinAuthMailTest {
        @Test
        @DisplayName("요청이 유효하면 인증 코드를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();
            String email = request.getEmail();

            // when
            ResultActions resultActions = requestSendJoinAuthMail(request);
            Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(email);

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(authMailCode)
                    .isPresent()
                    .get()
                    .satisfies(code -> {
                        Assertions.assertThat(code.getEmail()).isEqualTo(email);
                        Assertions.assertThat(code.isVerified()).isFalse();
                    });
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 인증 코드를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.auth.AuthCommandIntegrationTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder()
                    .email(invalidEmail)
                    .build();

            // when
            ResultActions resultActions = requestSendJoinAuthMail(request);

            // then
            resultActions.andExpect(status().isBadRequest());
            if (invalidEmail != null) {
                Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(invalidEmail);
                Assertions.assertThat(authMailCode).isNotPresent();
            }
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 인증 코드를 저장하지 않고 409를 반환한다")
        public void whenEmailAlreadyInUse() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();
            String email = request.getEmail();

            User user = UserFixture.builder()
                    .email(email)
                    .build();

            userCommandPort.save(user);

            // when
            ResultActions resultActions = requestSendJoinAuthMail(request);
            Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(email);

            // then
            resultActions.andExpect(status().isConflict());
            Assertions.assertThat(authMailCode).isNotPresent();
        }
    }

    @Nested
    @DisplayName("비밀번호 초기화 인증 메일 전송")
    class SendPasswordAuthMailTest {
        @Test
        @DisplayName("요청이 유효하면 인증 코드를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();
            String email = request.getEmail();

            User user = UserFixture.builder()
                    .email(email)
                    .build();

            userCommandPort.save(user);

            // when
            ResultActions resultActions = requestSendPasswordAuthMail(request);
            Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(email);

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(authMailCode)
                    .isPresent()
                    .get()
                    .satisfies(code -> {
                        Assertions.assertThat(code.getEmail()).isEqualTo(email);
                        Assertions.assertThat(code.isVerified()).isFalse();
                    });
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 인증 코드를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.auth.AuthCommandIntegrationTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder()
                    .email(invalidEmail)
                    .build();

            // when
            ResultActions resultActions = requestSendPasswordAuthMail(request);

            // then
            resultActions.andExpect(status().isBadRequest());
            if (invalidEmail != null) {
                Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(invalidEmail);
                Assertions.assertThat(authMailCode).isNotPresent();
            }
        }

        @Test
        @DisplayName("가입되지 않은 이메일이면 인증 코드를 저장하지 않고 404를 반환한다")
        public void whenEmailNotFound() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();
            String email = request.getEmail();

            // when
            ResultActions resultActions = requestSendPasswordAuthMail(request);
            Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(email);

            // then
            resultActions.andExpect(status().isNotFound());
            Assertions.assertThat(authMailCode).isNotPresent();
        }
    }

    @Nested
    @DisplayName("메일 인증 코드 검증")
    class ValidateAuthMailCodeTest {
        @Test
        @DisplayName("요청 코드가 일치하면 검증 성공 처리하고 200을 반환한다")
        public void whenCodeMatches() throws Exception {
            // given
            String code = "123456";
            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .code(code)
                    .build();
            authMailCodeCommandPort.save(authMailCode);

            AuthMailCodeValidateRequest request = AuthMailCodeValidateRequestFixture.builder()
                    .code(code)
                    .build();

            // when
            ResultActions resultActions = requestValidateAuthMailCode(request);
            Optional<AuthMailCode> savedCode = authMailCodeQueryPort.findById(authMailCode.getEmail());

            // then
            resultActions.andExpect(status().isOk());
            Assertions.assertThat(savedCode)
                    .isPresent()
                    .get()
                    .satisfies(c -> Assertions.assertThat(c.isVerified()).isTrue());
        }

        @Test
        @DisplayName("요청 코드가 일치하지 않으면 검증 실패 처리하고 200을 반환한다")
        public void whenCodeMismatches() throws Exception {
            // given
            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .code("123456")
                    .build();
            authMailCodeCommandPort.save(authMailCode);

            AuthMailCodeValidateRequest request = AuthMailCodeValidateRequestFixture.builder()
                    .code("999999")
                    .build();

            // when
            ResultActions resultActions = requestValidateAuthMailCode(request);
            Optional<AuthMailCode> savedCode = authMailCodeQueryPort.findById(authMailCode.getEmail());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.success").value(false));
            Assertions.assertThat(savedCode)
                    .isPresent()
                    .get()
                    .satisfies(c -> Assertions.assertThat(c.isVerified()).isFalse());
        }

        @ParameterizedTest
        @DisplayName("이메일이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.auth.AuthCommandIntegrationTest#invalidEmails")
        public void whenEmailInvalid(String invalidEmail) throws Exception {
            // given
            AuthMailCodeValidateRequest request = AuthMailCodeValidateRequestFixture.builder()
                    .email(invalidEmail)
                    .build();

            // when
            ResultActions resultActions = requestValidateAuthMailCode(request);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("코드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.auth.AuthCommandIntegrationTest#invalidCodes")
        public void whenCodeBlank(String invalidCode) throws Exception {
            // given
            AuthMailCodeValidateRequest request = AuthMailCodeValidateRequestFixture.builder()
                    .code(invalidCode)
                    .build();

            // when
            ResultActions resultActions = requestValidateAuthMailCode(request);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("인증 코드가 존재하지 않으면 410을 반환한다")
        public void whenCodeNotFound() throws Exception {
            // given
            AuthMailCodeValidateRequest request = AuthMailCodeValidateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestValidateAuthMailCode(request);

            // then
            resultActions.andExpect(status().isGone());
        }
    }

    private static Stream<String> invalidEmails() {
        return Stream.of(
                null,           // @NotNull 위반
                "invalid-email" // 이메일 형식 위반
        );
    }

    private static Stream<String> invalidCodes() {
        return Stream.of(
                null,   // @NotBlank 위반
                ""      // @NotBlank 위반
        );
    }

    private ResultActions requestValidateAuthMailCode(AuthMailCodeValidateRequest request) throws Exception {
        return mockMvc.perform(
                post(ApiPath.AUTH_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestSendJoinAuthMail(AuthMailRequest request) throws Exception {
        return mockMvc.perform(
                post(ApiPath.AUTH_MAIL_JOIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestSendPasswordAuthMail(AuthMailRequest request) throws Exception {
        return mockMvc.perform(
                post(ApiPath.AUTH_MAIL_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }
}
