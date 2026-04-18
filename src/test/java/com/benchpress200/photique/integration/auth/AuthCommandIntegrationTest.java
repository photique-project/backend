package com.benchpress200.photique.integration.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.api.command.request.AuthMailRequest;
import com.benchpress200.photique.auth.api.command.support.fixture.AuthMailRequestFixture;
import com.benchpress200.photique.auth.application.command.port.out.mail.MailSenderPort;
import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.infrastructure.exception.MailSendException;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("인증 커맨드 API 통합 테스트")
public class AuthCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AuthMailCodeQueryPort authMailCodeQueryPort;

    @Autowired
    private UserCommandPort userCommandPort;

    @MockitoSpyBean
    private MailSenderPort mailSenderPort;

    @MockitoSpyBean
    private AuthMailCodeCommandPort authMailCodeCommandPort;

    @BeforeEach
    void setUp() {
        authMailCodeCommandPort.deleteAll();
        Mockito.doNothing().when(mailSenderPort).sendMail(any());
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

        @Test
        @DisplayName("메일 전송에 실패하면 인증 코드를 저장하지 않고 500을 반환한다")
        public void whenMailSendFails() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();
            String email = request.getEmail();

            Mockito.doThrow(new MailSendException("메일 전송 실패")).when(mailSenderPort).sendMail(any());

            // when
            ResultActions resultActions = requestSendJoinAuthMail(request);
            Optional<AuthMailCode> authMailCode = authMailCodeQueryPort.findById(email);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(authMailCode).isNotPresent();
        }

        @Test
        @DisplayName("인증 코드 저장에 실패하면 500을 반환한다")
        public void whenSaveFails() throws Exception {
            // given
            AuthMailRequest request = AuthMailRequestFixture.builder().build();

            Mockito.doThrow(new DataAccessResourceFailureException("Redis 에러"))
                    .when(authMailCodeCommandPort).save(any());

            // when
            ResultActions resultActions = requestSendJoinAuthMail(request);

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private static Stream<String> invalidEmails() {
        return Stream.of(
                null,           // @NotNull 위반
                "invalid-email" // 이메일 형식 위반
        );
    }

    private ResultActions requestSendJoinAuthMail(AuthMailRequest request) throws Exception {
        return mockMvc.perform(
                post(ApiPath.AUTH_MAIL_JOIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }
}
