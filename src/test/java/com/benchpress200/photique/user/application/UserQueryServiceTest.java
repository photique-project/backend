package com.benchpress200.photique.user.application;

import com.benchpress200.photique.AbstractTestContainerConfig;
import com.benchpress200.photique.auth.domain.port.AuthenticationUserProviderPort;
import com.benchpress200.photique.user.application.exception.UserNotFoundException;
import com.benchpress200.photique.user.application.query.SearchUsersQuery;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.SearchUsersResult;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import com.benchpress200.photique.user.util.DummyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        String validNickname = DummyGenerator.generateNickname();
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
        String email = DummyGenerator.generateEmail();
        String nickname = DummyGenerator.generateNickname();
        String password = DummyGenerator.generatePassword();

        User user = User.builder()
                .email(email)
                .password(password)
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

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(userId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN
        UserDetailsResult userDetailsResult = userQueryService.getUserDetails(userId);

        // THEN
        Assertions.assertThat(userDetailsResult.getUserId()).isEqualTo(userId);
        Assertions.assertThat(userDetailsResult.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("getMyDetails 테스트 - 인증된 유저 조회 성공")
    void getMyDetails_테스트_인증된_유저_조회_성공() {
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

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(userId).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN
        MyDetailsResult myDetailsResult = userQueryService.getMyDetails();

        // THEN
        Assertions.assertThat(myDetailsResult.getUserId()).isEqualTo(userId);
        Assertions.assertThat(myDetailsResult.getEmail()).isEqualTo(email);
        Assertions.assertThat(myDetailsResult.getNickname()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("getMyDetails 테스트 - 인증된 유저 조회 실패")
    void getMyDetails_테스트_인증된_유저_조회_실패() {
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

        user = userRepository.save(user);
        Long userId = user.getId();

        Mockito.doReturn(userId * -1).when(authenticationUserProviderPort).getCurrentUserId();

        // WHEN and THEN
        Assertions.assertThatThrownBy(() -> userQueryService.getMyDetails()).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("searchUsers 테스트 - 유저 검색")
    void searchUsers_테스트_유저_검색() {
        // GIVEN
        long userId = DummyGenerator.generatePathVariable();
        int totalUsers = 60;
        int abStartingUserCount = 45;
        String keyword = "ab";
        String anotherKeyword = "cd";
        String sortBy = "nickname";

        int page = Integer.parseInt(DummyGenerator.generatePage());
        int size = Integer.parseInt(DummyGenerator.generateSize());
        Sort sort = Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= abStartingUserCount; i++) { // ab로 시작하는 유저 45명
            users.add(
                    User.builder()
                            .email(DummyGenerator.generateEmail())
                            .password(DummyGenerator.generatePassword())
                            .nickname((keyword + i))
                            .provider(Provider.LOCAL)
                            .role(Role.USER)
                            .build()
            );
        }

        for (int i = 1; i <= totalUsers - abStartingUserCount; i++) { // cd로 시작하는 유저 15명
            users.add(
                    User.builder()
                            .email(DummyGenerator.generateEmail())
                            .password(DummyGenerator.generatePassword())
                            .nickname(anotherKeyword + i)
                            .provider(Provider.LOCAL)
                            .role(Role.USER)
                            .build()
            );
        }

        userRepository.saveAll(users);

        Mockito.doReturn(userId).when(authenticationUserProviderPort).getCurrentUserId();

        SearchUsersQuery searchUsersQuery = SearchUsersQuery.builder()
                .keyword(keyword)
                .pageable(pageable)
                .build();

        // WHEN
        SearchUsersResult searchUsersResult = userQueryService.searchUsers(searchUsersQuery);

        // THEN
        Assertions.assertThat(searchUsersResult.getPage()).isEqualTo(page);
        Assertions.assertThat(searchUsersResult.getSize()).isEqualTo(size);
        Assertions.assertThat(searchUsersResult.getTotalElements()).isEqualTo(abStartingUserCount);
        Assertions.assertThat(searchUsersResult.isFirst()).isTrue();
        Assertions.assertThat(searchUsersResult.isLast()).isFalse();
        Assertions.assertThat(searchUsersResult.isHasNext()).isTrue();
        Assertions.assertThat(searchUsersResult.isHasPrevious()).isFalse();
        Assertions.assertThat(searchUsersResult.getUsers().size()).isEqualTo(size);
    }
}
