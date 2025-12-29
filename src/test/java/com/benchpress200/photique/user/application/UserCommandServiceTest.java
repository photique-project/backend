package com.benchpress200.photique.user.application;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.auth.domain.entity.AuthMailCode;
import com.benchpress200.photique.auth.infrastructure.persistence.redis.AuthMailCodeRepository;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.user.application.command.model.ResisterCommand;
import com.benchpress200.photique.user.application.command.model.UserDetailsUpdateCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordResetCommand;
import com.benchpress200.photique.user.application.command.model.UserPasswordUpdateCommand;
import com.benchpress200.photique.user.application.command.port.out.security.PasswordEncoderPort;
import com.benchpress200.photique.user.application.command.service.UserCommandService;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.exception.UserNotFoundException;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.UserRepository;
import com.benchpress200.photique.util.DummyGenerator;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@DisplayName("UserCommandService 테스트")
@ActiveProfiles("test")
@Import(TestContainerConfiguration.class)
public class UserCommandServiceTest {
    private static final String DUMMY_STRING = "a";
    private static final String MULTIPART_KEY_PROFILE_IMAGE = "profileImage";

    @MockitoSpyBean
    AuthMailCodeRepository authMailCodeRepository;

    @MockitoSpyBean
    ImageUploaderPort imageUploaderPort;

    @MockitoSpyBean
    UserRepository userRepository;

    @Autowired
    UserCommandService userCommandService;

    @Autowired
    PasswordEncoderPort passwordEncoderPort;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("join 커밋 테스트")
    void join_커밋_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        ResisterCommand resisterCommand = ResisterCommand.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        Mockito
                .doReturn(Optional.of(new AuthMailCode(email, "code", true, 1L)))
                .when(authMailCodeRepository)
                .findById(Mockito.any());

        // WHEN
        userCommandService.resister(resisterCommand);
        Optional<User> user = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(user.isPresent()).isTrue();
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이메일 인증 코드 조회 실패")
    void join_롤백_테스트_이메일_인증_코드_조회_실패() {
        // GIVEN

        // WHEN
        // THEN
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이메일 인증 코드 미인증")
    void join_롤백_테스트_이메일_인증_코드_미인증() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    @DisplayName("join 롤백 테스트 - 이미지 업로드 실패")
    void join_롤백_테스트_이미지_업로드_실패() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    @DisplayName("join 롤백 테스트 - MySQL 저장 실패")
    void join_롤백_테스트_MySQL_저장_실패() {
        // GIVEN
        // WHEN
        // THEN
    }

    @Test
    @DisplayName("updateUserDetails 커밋 테스트")
    void updateUserDetails_커밋_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();

        UserDetailsUpdateCommand userDetailsUpdateCommand = UserDetailsUpdateCommand.builder()
                .userId(savedUserId)
                .nickname(nicknameToUpdate)
                .introduction(introductionToUpdate)
                .build();

        // WHEN
        userCommandService.updateUserDetails(userDetailsUpdateCommand);
        Optional<User> updatedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(nicknameToUpdate);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(introductionToUpdate);
    }

    @Test
    @DisplayName("updateUserDetails 롤벡 테스트 - 유저 조회 실패")
    void updateUserDetails_롤백_테스트_유저_조회_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();

        UserDetailsUpdateCommand userDetailsUpdateCommand = UserDetailsUpdateCommand.builder()
                .userId(-1 * savedUserId) // 없는 id 가진 유저 데이터 조회하도록
                .nickname(nicknameToUpdate)
                .introduction(introductionToUpdate)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserDetails(userDetailsUpdateCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(nicknameToUpdate);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(introductionToUpdate);
    }

    @Test
    @DisplayName("updateUserDetails 롤벡 테스트 - 프로필 이미지 업데이트 실패")
    void updateUserDetails_롤백_테스트_프로필_이미지_업데이트_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String nicknameToUpdate = DummyGenerator.generateNickname();
        String introductionToUpdate = DummyGenerator.generateIntroduction();
        MockMultipartFile profileImageToUpdate = DummyGenerator.generateMockProfileImage(MULTIPART_KEY_PROFILE_IMAGE);

        UserDetailsUpdateCommand userDetailsUpdateCommand = UserDetailsUpdateCommand.builder()
                .userId(savedUserId)
                .nickname(nicknameToUpdate)
                .introduction(introductionToUpdate)
                .profileImage(profileImageToUpdate)
                .build();

        Mockito.doThrow(RuntimeException.class).when(imageUploaderPort)
                .upload(Mockito.any(), Mockito.any());

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserDetails(userDetailsUpdateCommand))
                .isInstanceOf(RuntimeException.class);
        Optional<User> updatedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getNickname()).isEqualTo(originalUser.get().getNickname());
        Assertions.assertThat(updatedUser.get().getNickname()).isNotEqualTo(nicknameToUpdate);
        Assertions.assertThat(updatedUser.get().getIntroduction()).isEqualTo(originalUser.get().getIntroduction());
        Assertions.assertThat(updatedUser.get().getIntroduction()).isNotEqualTo(introductionToUpdate);
        Assertions.assertThat(updatedUser.get().getProfileImage()).isEqualTo(originalUser.get().getProfileImage());
    }

    @Test
    @DisplayName("updateUserPassword 커밋 테스트")
    void updateUserPassword_커밋_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String passwordToUpdate = DummyGenerator.generatePassword();

        UserPasswordUpdateCommand userPasswordUpdateCommand = UserPasswordUpdateCommand.builder()
                .userId(savedUserId)
                .password(passwordToUpdate)
                .build();

        // WHEN
        userCommandService.updateUserPassword(userPasswordUpdateCommand);
        Optional<User> updatedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isNotEqualTo(updatedUser.get().getPassword());
        Assertions.assertThat(passwordEncoderPort.matches(passwordToUpdate, updatedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("updateUserPassword 롤백 테스트 - 유저 조회 실패")
    void updateUserPassword_롤백_테스트_유저_조회_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String passwordToUpdate = DummyGenerator.generatePassword();

        UserPasswordUpdateCommand userPasswordUpdateCommand = UserPasswordUpdateCommand.builder()
                .userId(-1 * savedUserId) // 없는 id 가진 유저 데이터 조회하도록
                .password(passwordToUpdate)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.updateUserPassword(userPasswordUpdateCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isEqualTo(updatedUser.get().getPassword());
    }

    @Test
    @DisplayName("resetUserPassword 커밋 테스트")
    void resetUserPassword_커밋_테스트() {
        // GIVEN

        // WHEN

        // THEN
    }

    @Test
    @DisplayName("resetUserPassword 롤백 테스트 - 유저 조회 실패")
    void resetUserPassword_롤백_테스트_유저_조회_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        Optional<User> originalUser = userRepository.findById(savedUserId);

        String passwordToUpdate = DummyGenerator.generatePassword();

        UserPasswordResetCommand userPasswordResetCommand = UserPasswordResetCommand.builder()
                .email(email.concat(DUMMY_STRING))
                .password(passwordToUpdate)
                .build();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.resetUserPassword(userPasswordResetCommand))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> updatedUser = userRepository.findByEmail(email);

        // THEN
        Assertions.assertThat(originalUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(originalUser.get().getPassword()).isEqualTo(updatedUser.get().getPassword());
    }

    @Test
    @DisplayName("withdraw 커밋 테스트")
    void withdraw_커밋_테스트() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        // WHEN
        userCommandService.withdraw(savedUserId);
        Optional<User> deletedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(deletedUser.isPresent()).isTrue();
        Assertions.assertThat(deletedUser.get().getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("withdraw 롤백 테스트 - 유저 조회 실패")
    void withdraw_롤백_테스트_유저_조회_실패() {
        // GIVEN
        String email = DummyGenerator.generateEmail();
        String password = DummyGenerator.generatePassword();
        String nickname = DummyGenerator.generateNickname();

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        long savedUserId = savedUser.getId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userCommandService.withdraw(savedUserId * -1))
                .isInstanceOf(UserNotFoundException.class);
        Optional<User> deletedUser = userRepository.findById(savedUserId);

        // THEN
        Assertions.assertThat(deletedUser.isPresent()).isTrue();
        Assertions.assertThat(deletedUser.get().getDeletedAt()).isNull();
    }
}
