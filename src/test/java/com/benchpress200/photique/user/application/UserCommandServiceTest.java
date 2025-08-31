package com.benchpress200.photique.user.application;

import com.benchpress200.photique.AbstractTestContainerConfig;
import com.benchpress200.photique.auth.domain.entity.AuthCode;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeExpirationException;
import com.benchpress200.photique.auth.domain.exception.MailAuthenticationCodeNotVerifiedException;
import com.benchpress200.photique.auth.domain.repository.AuthCodeRepository;
import com.benchpress200.photique.image.domain.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.JoinCommand;
import com.benchpress200.photique.user.application.command.ResetUserPasswordCommand;
import com.benchpress200.photique.user.application.command.UpdateUserDetailsCommand;
import com.benchpress200.photique.user.application.command.UpdateUserPasswordCommand;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.entity.UserSearch;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.port.PasswordEncoderPort;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.domain.repository.UserSearchRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@DisplayName("UserCommandService 테스트")
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

    @Autowired
    PasswordEncoderPort passwordEncoderPort;

    Long testUserId;

    static final String testUserEmail = "test@google.com";

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email(testUserEmail)
                .password("password")
                .nickname("dummy")
                .profileImage("profileImageUrl")
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();
    }

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

    @Test
    @DisplayName("updateUserDetails 커밋 테스트")
    void updateUserDetails_커밋_테스트() {
        // GIVEN
        Optional<User> originalUser = userRepository.findById(testUserId);

        String updateNickname = "updateNickname";
        String updateIntroduction = "introduction";
        String updateProfileImageUrl = "updateProfileImageUrl";
        MockMultipartFile updateProfileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        UpdateUserDetailsCommand updateUserDetailsCommand = UpdateUserDetailsCommand.builder()
                .userId(testUserId)
                .nickname(updateNickname)
                .introduction(updateIntroduction)
                .profileImage(updateProfileImage)
                .build();

        Mockito.doReturn(updateProfileImageUrl).when(imageUploaderPort)
                .update(Mockito.any(), Mockito.any(), Mockito.any());

        // WHEN
        userCommandService.updateUserDetails(updateUserDetailsCommand);
        Optional<User> updatedUser = userRepository.findById(testUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(updateNickname);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(updateIntroduction);
        Assertions.assertThat(updatedUser.get().getProfileImage()).isNotEqualTo(originalUser.get().getProfileImage());
        Assertions.assertThat(updatedUser.get().getProfileImage()).isEqualTo(updateProfileImageUrl);
    }

    @Test
    @DisplayName("updateUserDetails 롤벡 테스트 - 유저 조회 실패")
    void updateUserDetails_롤백_테스트_유저_조회_실패() {
        // GIVEN
        Optional<User> originalUser = userRepository.findById(testUserId);

        String updateNickname = "updateNickname";
        String updateIntroduction = "introduction";
        String updateProfileImageUrl = "updateProfileImageUrl";
        MockMultipartFile updateProfileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        UpdateUserDetailsCommand updateUserDetailsCommand = UpdateUserDetailsCommand.builder()
                .userId(-1 * testUserId) // 없는 id 가진 유저 데이터 조회하도록
                .nickname(updateNickname)
                .introduction(updateIntroduction)
                .profileImage(updateProfileImage)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserDetails(updateUserDetailsCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findById(testUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(updateNickname);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(updateIntroduction);
        Assertions.assertThat(updatedUser.get().getProfileImage()).isEqualTo(originalUser.get().getProfileImage());
        Assertions.assertThat(updatedUser.get().getProfileImage()).isNotEqualTo(updateProfileImageUrl);
    }

    @Test
    @DisplayName("updateUserDetails 롤벡 테스트 - 프로필 이미지 업데이트 실패")
    void updateUserDetails_롤백_테스트_프로필_이미지_업데이트_실패() {
        // GIVEN
        Optional<User> originalUser = userRepository.findById(testUserId);

        String updateNickname = "updateNickname";
        String updateIntroduction = "introduction";
        String updateProfileImageUrl = "updateProfileImageUrl";
        MockMultipartFile updateProfileImage = new MockMultipartFile("profileImage", "test.png",
                "image/png", "dummy".getBytes());

        UpdateUserDetailsCommand updateUserDetailsCommand = UpdateUserDetailsCommand.builder()
                .userId(testUserId)
                .nickname(updateNickname)
                .introduction(updateIntroduction)
                .profileImage(updateProfileImage)
                .build();

        Mockito.doThrow(RuntimeException.class).when(imageUploaderPort)
                .update(Mockito.any(), Mockito.any(), Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserDetails(updateUserDetailsCommand))
                .isInstanceOf(RuntimeException.class);
        Optional<User> updatedUser = userRepository.findById(testUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(updateNickname);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(updateIntroduction);
        Assertions.assertThat(updatedUser.get().getProfileImage()).isEqualTo(originalUser.get().getProfileImage());
        Assertions.assertThat(updatedUser.get().getProfileImage()).isNotEqualTo(updateProfileImageUrl);
    }

    @Test
    @DisplayName("updateUserPassword 커밋 테스트")
    void updateUserPassword_커밋_테스트() {
        // GIVEN
        Optional<User> originalUser = userRepository.findById(testUserId);
        String updatePassword = "updatePassword";

        UpdateUserPasswordCommand updateUserPasswordCommand = UpdateUserPasswordCommand.builder()
                .userId(testUserId)
                .password(updatePassword)
                .build();

        // WHEN
        userCommandService.updateUserPassword(updateUserPasswordCommand);
        Optional<User> updatedUser = userRepository.findById(testUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isNotEqualTo(updatedUser.get().getPassword());
        Assertions.assertThat(passwordEncoderPort.matches(updatePassword, updatedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("updateUserPassword 롤백 테스트 - 유저 조회 실패")
    void updateUserPassword_롤백_테스트_유저_조회_실패() {
        // GIVEN
        Optional<User> originalUser = userRepository.findById(testUserId);
        String updatePassword = "updatePassword";

        UpdateUserPasswordCommand updateUserPasswordCommand = UpdateUserPasswordCommand.builder()
                .userId(-1 * testUserId) // 없는 id 가진 유저 데이터 조회하도록
                .password(updatePassword)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserPassword(updateUserPasswordCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findById(testUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isEqualTo(updatedUser.get().getPassword());
    }

    @Test
    @DisplayName("resetUserPassword 커밋 테스트")
    void resetUserPassword_커밋_테스트() {
        // GIVEN
        Optional<User> originalUser = userRepository.findByEmail(testUserEmail);
        String updatePassword = "updatePassword";

        ResetUserPasswordCommand resetUserPasswordCommand = ResetUserPasswordCommand.builder()
                .email(testUserEmail)
                .password(updatePassword)
                .build();

        // WHEN
        userCommandService.resetUserPassword(resetUserPasswordCommand);
        Optional<User> updatedUser = userRepository.findByEmail(testUserEmail);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isNotEqualTo(updatedUser.get().getPassword());
        Assertions.assertThat(passwordEncoderPort.matches(updatePassword, updatedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("resetUserPassword 롤백 테스트 - 유저 조회 실패")
    void resetUserPassword_롤백_테스트_유저_조회_실패() {
        // GIVEN
        Optional<User> originalUser = userRepository.findByEmail(testUserEmail);
        String updatePassword = "updatePassword";

        ResetUserPasswordCommand resetUserPasswordCommand = ResetUserPasswordCommand.builder()
                .email(testUserEmail + "dummy")
                .password(updatePassword)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.resetUserPassword(resetUserPasswordCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findByEmail(testUserEmail);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isEqualTo(updatedUser.get().getPassword());
    }
}
