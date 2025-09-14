package com.benchpress200.photique.user.application;

import com.benchpress200.photique.AbstractTestContainerConfig;
import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@DisplayName("UserQueryService 테스트")
@ActiveProfiles("test")
public class UserQueryServiceTest extends AbstractTestContainerConfig {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserQueryService userQueryService;

    @MockitoSpyBean
    AuthenticationUserProviderPort authenticationUserProviderPort;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("validateNickname 테스트 - 중복되지 않은 닉네임")
    void validateNickname_테스트_중복되지_않은_닉네임() {
        // GIVEN
        String validNickname = "nickname";
        ValidateNicknameQuery validateNicknameQuery = ValidateNicknameQuery.builder()
                .nickname(validNickname)
                .build();

        // WHEN
        ValidateNicknameResult validateNicknameResult = userQueryService.validateNickname(validateNicknameQuery);
        boolean result = validateNicknameResult.isDuplicated();

        // THEN
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateNickname 테스트 - 중복된 닉네임")
    void validateNickname_테스트_중복된_닉네임() {
        // GIVEN
        String nickname = "nickname";
        User user = User.builder()
                .email("example@example.com")
                .password("password12!@")
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        userRepository.save(user);

        ValidateNicknameQuery validateNicknameQuery = ValidateNicknameQuery.builder()
                .nickname(nickname)
                .build();

        // WHEN
        ValidateNicknameResult validateNicknameResult = userQueryService.validateNickname(validateNicknameQuery);
        boolean result = validateNicknameResult.isDuplicated();

        // THEN
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("getUserDetails 테스트 - 저장한 유저 조회")
    void getUserDetails_테스트_저장한_유저_조회() {
        // GIVEN
        Long InvalidUserId = 0L;
        String email = "example@example.com";
        String password = "password12!@";
        String nickname = "nickname";

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(InvalidUserId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN
        UserDetailsResult userDetailsResult = userQueryService.getUserDetails(userId);

        // THEN
        Assertions.assertThat(userDetailsResult.getUserId()).isEqualTo(userId);
        Assertions.assertThat(userDetailsResult.getNickname()).isEqualTo(nickname);
        Assertions.assertThat(userDetailsResult.getIntroduction()).isEqualTo(null);
        Assertions.assertThat(userDetailsResult.getProfileImage()).isEqualTo(null);
        Assertions.assertThat(userDetailsResult.getSingleWorkCount()).isEqualTo(0L);
        Assertions.assertThat(userDetailsResult.getExhibitionCount()).isEqualTo(0L);
        Assertions.assertThat(userDetailsResult.getFollowerCount()).isEqualTo(0L);
        Assertions.assertThat(userDetailsResult.getFollowingCount()).isEqualTo(0L);
        Assertions.assertThat(userDetailsResult.isFollowing()).isFalse();
    }

    @Test
    @DisplayName("getMyDetails 테스트 - 인증된 유저 조회 성공")
    void getMyDetails_테스트_인증된_유저_조회_성공() {
        // GIVEN
        String email = "example@example.com";
        String password = "password12!@";
        String nickname = "nickname";
        String profileImage = "profileImage";

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(userId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN
        MyDetailsResult myDetailsResult = userQueryService.getMyDetails();

        // THEN
        Assertions.assertThat(myDetailsResult.getUserId()).isEqualTo(userId);
        Assertions.assertThat(myDetailsResult.getEmail()).isEqualTo(email);
        Assertions.assertThat(myDetailsResult.getNickname()).isEqualTo(nickname);
        Assertions.assertThat(myDetailsResult.getIntroduction()).isEqualTo(null);
        Assertions.assertThat(myDetailsResult.getProfileImage()).isEqualTo(profileImage);
        Assertions.assertThat(myDetailsResult.getSingleWorkCount()).isEqualTo(0L);
        Assertions.assertThat(myDetailsResult.getExhibitionCount()).isEqualTo(0L);
        Assertions.assertThat(myDetailsResult.getFollowerCount()).isEqualTo(0L);
        Assertions.assertThat(myDetailsResult.getFollowingCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("getMyDetails 테스트 - 인증된 유저 조회 실패")
    void getMyDetails_테스트_인증된_유저_조회_실패() {
        // GIVEN
        String email = "example@example.com";
        String password = "password12!@";
        String nickname = "nickname";
        String profileImage = "profileImage";

        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .provider(Provider.LOCAL)
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(userId * -1).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userQueryService.getMyDetails()).isInstanceOf(UserNotFoundException.class);
    }
}
