package com.benchpress200.photique.user.application.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.benchpress200.photique.auth.application.query.port.out.persistence.AuthMailCodeQueryPort;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.integration.auth.support.fixture.AuthMailCodeFixture;
import com.benchpress200.photique.outbox.application.factory.OutboxEventFactory;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.support.base.BaseServiceTest;
import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import com.benchpress200.photique.user.application.command.port.out.event.UserEventPublishPort;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.command.port.out.security.PasswordEncoderPort;
import com.benchpress200.photique.user.application.command.service.UserCommandService;
import com.benchpress200.photique.user.application.command.support.fixture.ResisterCommandFixture;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.exception.DuplicatedUserException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("유저 커맨드 서비스 테스트")
public class UserCommandServiceTest extends BaseServiceTest {
    @InjectMocks
    private UserCommandService userCommandService;

    @Mock
    private UserCommandPort userCommandPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserEventPublishPort userEventPublishPort;

    @Mock
    private ImageUploaderPort imageUploaderPort;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @Mock
    private AuthMailCodeQueryPort authMailCodeQueryPort;

    @Mock
    private OutboxEventPort outboxEventPort;

    @Mock
    private OutboxEventFactory outboxEventFactory;

    @Nested
    @DisplayName("회원가입")
    class RegisterTest {
        @Test
        @DisplayName("프로필 이미지 없이 처리에 성공한다")
        public void whenCommandValidWithoutProfileImage() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doReturn("encodedPassword").when(passwordEncoderPort).encode(any());
            doReturn(null).when(userCommandPort).save(any());

            // when
            userCommandService.resister(command);

            // then
            verify(authMailCodeQueryPort).findById(command.getEmail());
            verify(passwordEncoderPort).encode(command.getPassword());
            verify(imageUploaderPort, never()).upload(any(), any());
            verify(userCommandPort).save(any());
        }

        @Test
        @DisplayName("프로필 이미지와 함께 처리에 성공한다")
        public void whenCommandValidWithProfileImage() {
            // given
            MultipartFile profileImage = mock(MultipartFile.class);
            ResisterCommand command = ResisterCommandFixture.builder().profileImage(profileImage).build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doReturn("encodedPassword").when(passwordEncoderPort).encode(any());
            doReturn("https://example.com/image.jpg").when(imageUploaderPort).upload(any(), any());
            doReturn(null).when(userCommandPort).save(any());

            // when
            userCommandService.resister(command);

            // then
            verify(authMailCodeQueryPort).findById(command.getEmail());
            verify(passwordEncoderPort).encode(command.getPassword());
            verify(imageUploaderPort).upload(any(), any());
            verify(userEventPublishPort).publishUserProfileImageUploadEvent(any());
            verify(userCommandPort).save(any());
        }

        @Test
        @DisplayName("인증 코드가 만료되었으면 MailAuthenticationCodeExpirationException을 던진다")
        public void whenAuthMailCodeExpired() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();

            doReturn(Optional.empty()).when(authMailCodeQueryPort).findById(any());

            // when & then
            assertThrows(
                    MailAuthenticationCodeExpirationException.class,
                    () -> userCommandService.resister(command)
            );
            verify(userCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("인증이 완료되지 않은 코드이면 MailAuthenticationCodeNotVerifiedException을 던진다")
        public void whenNotVerified() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(false).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());

            // when & then
            assertThrows(
                    MailAuthenticationCodeNotVerifiedException.class,
                    () -> userCommandService.resister(command)
            );
            verify(userCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("비밀번호 인코딩에 실패하면 예외를 던진다")
        public void whenPasswordEncodeFails() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doThrow(new RuntimeException()).when(passwordEncoderPort).encode(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userCommandService.resister(command)
            );
            verify(userCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("이미지 업로드에 실패하면 예외를 던진다")
        public void whenImageUploadFails() {
            // given
            MultipartFile profileImage = mock(MultipartFile.class);
            ResisterCommand command = ResisterCommandFixture.builder().profileImage(profileImage).build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doReturn("encodedPassword").when(passwordEncoderPort).encode(any());
            doThrow(new RuntimeException()).when(imageUploaderPort).upload(any(), any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userCommandService.resister(command)
            );
            verify(userCommandPort, never()).save(any());
        }

        @Test
        @DisplayName("중복된 이메일 또는 닉네임이면 DuplicatedUserException을 던진다")
        public void whenDuplicated() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doReturn("encodedPassword").when(passwordEncoderPort).encode(any());
            doThrow(new DataIntegrityViolationException("duplicate")).when(userCommandPort).save(any());

            // when & then
            assertThrows(
                    DuplicatedUserException.class,
                    () -> userCommandService.resister(command)
            );
        }

        @Test
        @DisplayName("유저 저장에 실패하면 예외를 던진다")
        public void whenSaveFails() {
            // given
            ResisterCommand command = ResisterCommandFixture.builder().build();
            AuthMailCode authMailCode = AuthMailCodeFixture.builder().isVerified(true).build();

            doReturn(Optional.of(authMailCode)).when(authMailCodeQueryPort).findById(any());
            doReturn("encodedPassword").when(passwordEncoderPort).encode(any());
            doThrow(new RuntimeException()).when(userCommandPort).save(any());

            // when & then
            assertThrows(
                    RuntimeException.class,
                    () -> userCommandService.resister(command)
            );
        }
    }
}
