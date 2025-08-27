package com.benchpress200.photique.user.application;

import com.benchpress200.photique.AbstractTestContainerConfig;
import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.repository.AuthCodeRepository;
import com.benchpress200.photique.image.domain.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.domain.repository.UserSearchRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@DisplayName("유저 도메인 Command 테스트")
@ActiveProfiles("test")
public class UserCommandServiceTest extends AbstractTestContainerConfig {
    @MockitoSpyBean
    AuthCodeRepository authCodeRepository;

    @MockitoSpyBean
    ImageUploaderPort imageUploaderPort;

    @MockitoSpyBean
    UserRepository userRepository;

    @MockitoSpyBean
    UserSearchRepository userSearchRepository;

    @Autowired
    UserCommandService userCommandService;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userSearchRepository.deleteAll();
    }

    @Test
    @DisplayName("join 커밋 테스트")
    void join_커밋_테스트() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthCode("email", "code", true, 1L)))
                .when(authCodeRepository)
                .findById(Mockito.any());

        // WHEN
        userCommandService.join(joinCommand);
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isTrue();

        // WHEN
        Optional<UserSearch> userSearch = userSearchRepository.findById(user.get().getId());

        // THEN
        Assertions.assertThat(userSearch.isPresent()).isTrue();
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이메일 인증 코드 조회 실패")
    void join_롤백_테스트_이메일_인증_코드_조회_실패() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.empty())
                .when(authCodeRepository)
                .findById(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.join(joinCommand))
                .isInstanceOf(MailAuthenticationCodeExpirationException.class);

        // WHEN
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isFalse();

        // WHEN
        Iterable<UserSearch> userSearch = userSearchRepository.findAll();

        // THEN
        Assertions.assertThat(userSearch).hasSize(0);
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이메일 인증 코드 미인증")
    void join_롤백_테스트_이메일_인증_코드_미인증() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthCode("email", "code", false, 1L)))
                .when(authCodeRepository)
                .findById(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.join(joinCommand))
                .isInstanceOf(MailAuthenticationCodeNotVerifiedException.class);

        // WHEN
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isFalse();

        // WHEN
        Iterable<UserSearch> userSearch = userSearchRepository.findAll();

        // THEN
        Assertions.assertThat(userSearch).hasSize(0);
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이미지 업로드 실패")
    void join_롤백_테스트_이미지_업로드_실패() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";
        MultipartFile profileImage = Mockito.mock(MultipartFile.class);

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthCode("email", "code", true, 1L)))
                .when(authCodeRepository)
                .findById(Mockito.any());

        Mockito.doThrow(RuntimeException.class).when(imageUploaderPort).upload(Mockito.any(), Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.join(joinCommand))
                .isInstanceOf(RuntimeException.class);

        // WHEN
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isFalse();

        // WHEN
        Iterable<UserSearch> userSearch = userSearchRepository.findAll();

        // THEN
        Assertions.assertThat(userSearch).hasSize(0);
    }

    @Test
    @DisplayName("join 롤백 테스트 - MySQL 저장 실패")
    void join_롤백_테스트_MySQL_저장_실패() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthCode("email", "code", true, 1L)))
                .when(authCodeRepository)
                .findById(Mockito.any());

        Mockito.doThrow(RuntimeException.class).when(userRepository).save(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.join(joinCommand))
                .isInstanceOf(RuntimeException.class);

        // WHEN
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isFalse();

        // WHEN
        Iterable<UserSearch> userSearch = userSearchRepository.findAll();

        // THEN
        Assertions.assertThat(userSearch).hasSize(0);
    }

    @Test
    @DisplayName("join 롤백 테스트 - Elasticsearch 저장 실패")
    void join_롤백_테스트_Elasticsearch_저장_실패() {
        // GIVEN
        String email = "example@google.com";
        String password = "password12!@";
        String nickname = "nickname";

        JoinCommand joinCommand = JoinCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthCode("email", "code", true, 1L)))
                .when(authCodeRepository)
                .findById(Mockito.any());

        Mockito.doThrow(RuntimeException.class).when(userSearchRepository).save(Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.join(joinCommand))
                .isInstanceOf(RuntimeException.class);

        // WHEN
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isFalse();

        // WHEN
        Iterable<UserSearch> userSearch = userSearchRepository.findAll();

        // THEN
        Assertions.assertThat(userSearch).hasSize(0);
    }
}
