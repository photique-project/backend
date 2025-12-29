package com.benchpress200.photique.auth.application;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.auth.application.command.model.AuthMailCodeValidateCommand;
import com.benchpress200.photique.auth.application.command.model.AuthMailCommand;
import com.benchpress200.photique.auth.application.command.model.AuthTokenRefreshCommand;
import com.benchpress200.photique.auth.application.command.result.AuthMailCodeValidateResult;
import com.benchpress200.photique.auth.application.command.result.AuthTokenResult;
import com.benchpress200.photique.auth.application.command.service.AuthCommandService;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.enumeration.TokenValidationStatus;
import com.benchpress200.photique.auth.domain.exception.EmailAlreadyInUseException;
import com.benchpress200.photique.auth.domain.exception.EmailNotFoundException;
import com.benchpress200.photique.auth.domain.exception.InvalidRefreshTokenException;
import com.benchpress200.photique.auth.domain.exception.VerificationCodeNotFoundException;
import com.benchpress200.photique.auth.application.command.port.out.mail.MailSenderPort;
import com.benchpress200.photique.auth.application.command.port.out.persistence.AuthMailCodeCommandPort;
import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.auth.domain.vo.TokenValidationResult;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import com.benchpress200.photique.util.DummyGenerator;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@DisplayName("AuthCommandService 테스트")
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
public class AuthCommandServiceTest {
    private static final int AUTH_MAIL_CODE_LENGTH = 6;

    @Autowired
    AuthCommandService authCommandService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    AuthMailCodeCommandPort authMailCodeCommandPort;

    @MockitoBean
    MailSenderPort mailSenderPort;

    @MockitoSpyBean
    AuthenticationTokenManagerPort authenticationTokenManagerPort;


    @Test
    @DisplayName("sendJoinAuthMail 성공 테스트")
    void sendJoinAuthMail_sendJoinAuthMail_성공_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        AuthMailCommand authMailCommand = AuthMailCommand.builder()
                .email(email)
                .build();

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(Boolean.FALSE);

        String dummyCode = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);

        // WHEN
        authCommandService.sendJoinAuthMail(authMailCommand);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.times(1))
                .save(Mockito.any(AuthMailCode.class));
    }

    @Test
    @DisplayName("sendJoinAuthMail 실패 테스트 - 이미 가입된 이메일")
    void sendJoinAuthMail_실패_테스트_이미_가입된_이메일() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        AuthMailCommand authMailCommand = AuthMailCommand.builder()
                .email(email)
                .build();

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(Boolean.TRUE);

        // WHEN
        Assertions.assertThatThrownBy(() -> authCommandService.sendJoinAuthMail(authMailCommand))
                .isInstanceOf(EmailAlreadyInUseException.class);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.never())
                .save(Mockito.any(AuthMailCode.class));
    }

    @Test
    @DisplayName("sendPasswordAuthMail 성공 테스트")
    void sendPasswordAuthMail_성공_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        AuthMailCommand authMailCommand = AuthMailCommand.builder()
                .email(email)
                .build();

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(Boolean.TRUE);

        String dummyCode = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);

        // WHEN
        authCommandService.sendPasswordAuthMail(authMailCommand);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.times(1))
                .save(Mockito.any(AuthMailCode.class));
    }

    @Test
    @DisplayName("sendPasswordAuthMail 실패 테스트 - 가입된 적 없는 이메일")
    void sendPasswordAuthMail_실패_테스트_가입된_적_없는_이메일() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        AuthMailCommand authMailCommand = AuthMailCommand.builder()
                .email(email)
                .build();

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(Boolean.FALSE);

        // WHEN
        Assertions.assertThatThrownBy(() -> authCommandService.sendPasswordAuthMail(authMailCommand))
                .isInstanceOf(EmailNotFoundException.class);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.never())
                .save(Mockito.any(AuthMailCode.class));
    }

    @Test
    @DisplayName("validateAuthMailCode 성공 테스트 - 유효한 인증 코드")
    void validateAuthMailCode_성공_테스트_유효한_인증_코드() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String code = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        AuthMailCodeValidateCommand authMailCodeValidationCommand = AuthMailCodeValidateCommand.builder()
                .email(email)
                .code(code)
                .build();

        AuthMailCode emailAuthCode = AuthMailCode.of(email, code);
        Mockito.when(authMailCodeCommandPort.findById(email))
                .thenReturn(Optional.of(emailAuthCode));

        // WHEN
        AuthMailCodeValidateResult authMailCodeValidationResult = authCommandService.validateAuthMailCode(
                authMailCodeValidationCommand);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.times(1))
                .save(Mockito.any(AuthMailCode.class));
        Assertions.assertThat(authMailCodeValidationResult.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("validateAuthMailCode 성공 테스트 - 유효하지 않은 인증 코드")
    void validateAuthMailCode_성공_테스트_유효하지_않은_인증_코드() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String code = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        String anotherCode = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        AuthMailCodeValidateCommand authMailCodeValidateCommand = AuthMailCodeValidateCommand.builder()
                .email(email)
                .code(code)
                .build();

        AuthMailCode emailAuthCode = AuthMailCode.of(email, anotherCode);
        Mockito.when(authMailCodeCommandPort.findById(email))
                .thenReturn(Optional.of(emailAuthCode));

        // WHEN
        AuthMailCodeValidateResult authMailCodeValidationResult = authCommandService.validateAuthMailCode(
                authMailCodeValidateCommand);

        // THEN
        Mockito.verify(authMailCodeCommandPort, Mockito.never())
                .save(Mockito.any(AuthMailCode.class));
        Assertions.assertThat(authMailCodeValidationResult.isSuccess()).isFalse();
    }

    @Test
    @DisplayName("validateAuthMailCode 실패 테스트 - 인증 코드 조회 실패")
    void validateAuthMailCode_실패_테스트_인증_코드_조회_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String code = DummyGenerator.generateRandomNumberString(AUTH_MAIL_CODE_LENGTH);
        AuthMailCodeValidateCommand authMailCodeValidateCommand = AuthMailCodeValidateCommand.builder()
                .email(email)
                .code(code)
                .build();

        Mockito.when(authMailCodeCommandPort.findById(email))
                .thenThrow(new VerificationCodeNotFoundException());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> authCommandService.validateAuthMailCode(authMailCodeValidateCommand))
                .isInstanceOf(VerificationCodeNotFoundException.class);

        Mockito.verify(authMailCodeCommandPort, Mockito.never())
                .save(Mockito.any(AuthMailCode.class));
    }

    @Test
    @DisplayName("refreshAuthToken 성공 테스트")
    void refreshAuthToken_성공_테스트() {
        // GIVEN
        Long userId = DummyGenerator.generateResourceId();
        String role = DummyGenerator.generateRole();
        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);
        String refreshToken = authenticationTokens.getRefreshToken();
        AuthTokenRefreshCommand authTokenRefreshCommand = AuthTokenRefreshCommand.of(refreshToken);

        // WHEN
        AuthTokenResult authTokenResult = authCommandService.refreshAuthToken(authTokenRefreshCommand);

        // THEN
        Mockito.verify(authenticationTokenManagerPort, Mockito.times(2))
                .issueTokens(Mockito.any(), Mockito.any());

        Assertions.assertThat(authTokenResult).isNotNull();
    }

    @Test
    @DisplayName("refreshAuthToken 실패 테스트 - 유효하지 않은 리프레쉬 토큰")
    void refreshAuthToken_실패_테스트_유효하지_않은_리프레쉬_토큰() {
        // GIVEN
        Long userId = DummyGenerator.generateResourceId();
        String role = DummyGenerator.generateRole();
        AuthenticationTokens authenticationTokens = authenticationTokenManagerPort.issueTokens(userId, role);
        String refreshToken = authenticationTokens.getRefreshToken();
        AuthTokenRefreshCommand authTokenRefreshCommand = AuthTokenRefreshCommand.of(refreshToken);
        TokenValidationResult tokenValidationResult = TokenValidationResult.builder()
                .userId(userId)
                .role(role)
                .status(TokenValidationStatus.INVALID)
                .build();

        Mockito.when(authenticationTokenManagerPort.validateToken(refreshToken))
                .thenReturn(tokenValidationResult);

        // WHEN
        Assertions.assertThatThrownBy(() -> authCommandService.refreshAuthToken(authTokenRefreshCommand))
                .isInstanceOf(InvalidRefreshTokenException.class);

        // THEN
        Mockito.verify(authenticationTokenManagerPort, Mockito.times(1))
                .issueTokens(Mockito.any(), Mockito.any());
    }
}
