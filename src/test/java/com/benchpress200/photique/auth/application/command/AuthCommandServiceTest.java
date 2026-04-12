package com.benchpress200.photique.auth.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.port.out.mail.MailSenderPort;
import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.service.AuthCommandService;
import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.application.support.fixture.AuthMailCodeValidateCommandFixture;
import com.benchpress200.photique.auth.application.support.fixture.AuthMailCommandFixture;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.domain.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("인증 커맨드 서비스 테스트")
public class AuthCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private AuthCommandService authCommandService;

    @Mock
    private AuthMailCodeCommandPort authMailCodeCommandPort;

    @Mock
    private AuthMailCodeQueryPort authMailCodeQueryPort;

    @Mock
    private MailSenderPort mailSenderPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Nested
    @DisplayName("인증 메일 코드 검증")
    class ValidateAuthMailCodeTest {
        @Test
        @DisplayName("코드가 일치하면 검증 성공 결과를 반환하고 인증 상태를 저장한다")
        public void whenCodeValid() {
            // given
            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .code("123456")
                    .build();

            AuthMailCodeValidateCommand command = AuthMailCodeValidateCommandFixture.builder()
                    .email(authMailCode.getEmail())
                    .code("123456")
                    .build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());

            // when
            AuthMailCodeValidateResult result = authCommandService.validateAuthMailCode(command);

            // then
            verify(authMailCodeQueryPort).findById(command.getEmail());
            verify(authMailCodeCommandPort).save(authMailCode);
            assertThat(result.isSuccess()).isTrue();
        }

        @Test
        @DisplayName("코드가 일치하지 않으면 검증 실패 결과를 반환하고 저장하지 않는다")
        public void whenCodeInvalid() {
            // given
            AuthMailCode authMailCode = AuthMailCodeFixture.builder()
                    .code("123456")
                    .build();

            AuthMailCodeValidateCommand command = AuthMailCodeValidateCommandFixture.builder()
                    .email(authMailCode.getEmail())
                    .code("000000")
                    .build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());

            // when
            AuthMailCodeValidateResult result = authCommandService.validateAuthMailCode(command);

            // then
            verify(authMailCodeQueryPort).findById(command.getEmail());
            verify(authMailCodeCommandPort, never()).save(any());
            assertThat(result.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("인증 코드가 존재하지 않거나 만료되었으면 VerificationCodeNotFoundException을 던진다")
        public void whenAuthMailCodeNotFound() {
            // given
            AuthMailCodeValidateCommand command = AuthMailCodeValidateCommandFixture.builder().build();

            doReturn(Optional.empty()).when(authMailCodeQueryPort).findById(any());

            // when & then
            assertThrows(
                    VerificationCodeNotFoundException.class,
                    () -> authCommandService.validateAuthMailCode(command)
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 초기화 인증 메일 전송")
    class SendPasswordAuthMailTest {
        @Test
        @DisplayName("유저가 존재하면 인증 메일을 전송하고 인증 코드를 저장한다")
        public void whenUserExists() {
            // given
            AuthMailCommand command = AuthMailCommandFixture.builder().build();

            doReturn(true).when(userQueryPort).existsByEmail(any());
            doNothing().when(mailSenderPort).sendMail(any());

            // when
            authCommandService.sendPasswordAuthMail(command);

            // then
            verify(userQueryPort).existsByEmail(command.getEmail());
            verify(mailSenderPort).sendMail(any());
            verify(authMailCodeCommandPort).save(any());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 EmailNotFoundException을 던진다")
        public void whenUserNotFound() {
            // given
            AuthMailCommand command = AuthMailCommandFixture.builder().build();

            doReturn(false).when(userQueryPort).existsByEmail(any());

            // when & then
            assertThrows(
                    EmailNotFoundException.class,
                    () -> authCommandService.sendPasswordAuthMail(command)
            );
            verify(mailSenderPort, never()).sendMail(any());
        }
    }
}
