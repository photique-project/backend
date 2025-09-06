package com.benchpress200.photique.user.application;

import com.benchpress200.photique.AbstractTestContainerConfig;
import com.benchpress200.photique.user.application.query.ValidateNicknameQuery;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.enumeration.Provider;
import com.benchpress200.photique.user.domain.enumeration.Role;
import com.benchpress200.photique.user.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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

    @Test
    @DisplayName("validateNickname 유효한 닉네임 테스트")
    void validateNickname_유효한_닉네임_테스트() {
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
    @DisplayName("validateNickname 중복된 닉네임 테스트")
    void validateNickname_중복된_닉네임_테스트() {
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
}
