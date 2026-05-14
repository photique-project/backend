package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("유저 쿼리 API 통합 테스트")
public class UserQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    private User savedUser;
    private String accessToken;

    @AfterEach
    void cleanUp() {
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("유저 정보 상세 조회")
    class GetUserDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 유저 정보를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            User targetUser = userCommandPort.save(UserFixture.builder()
                    .email("target@example.com")
                    .nickname("타겟유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            // when
            ResultActions resultActions = requestGetUserDetailsAuthenticated(targetUser.getId());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(targetUser.getId()))
                    .andExpect(jsonPath("$.data.nickname").value("타겟유저"))
                    .andExpect(jsonPath("$.data.singleWorkCount").value(0))
                    .andExpect(jsonPath("$.data.exhibitionCount").value(0))
                    .andExpect(jsonPath("$.data.followerCount").value(0))
                    .andExpect(jsonPath("$.data.followingCount").value(0))
                    .andExpect(jsonPath("$.data.isFollowing").value(false));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            User targetUser = userCommandPort.save(UserFixture.builder().build());

            // when
            ResultActions resultActions = requestGetUserDetails(targetUser.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 404를 반환한다")
        public void whenUserNotFound() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();
            Long nonExistentUserId = savedUser.getId() + 9999L;

            // when
            ResultActions resultActions = requestGetUserDetailsAuthenticated(nonExistentUserId);

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("닉네임 중복검사")
    class ValidateNicknameTest {
        @Test
        @DisplayName("닉네임이 중복되지 않으면 isDuplicated가 false이고 200을 반환한다")
        public void whenNicknameNotDuplicated() throws Exception {
            // given
            String nickname = "새닉네임";
            userCommandPort.save(UserFixture.builder().build());

            // when
            ResultActions resultActions = requestValidateNickname(nickname);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isDuplicated").value(false));
        }

        @Test
        @DisplayName("닉네임이 중복되면 isDuplicated가 true이고 200을 반환한다")
        public void whenNicknameDuplicated() throws Exception {
            // given
            User user = userCommandPort.save(UserFixture.builder().build());
            String nickname = user.getNickname();

            // when
            ResultActions resultActions = requestValidateNickname(nickname);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isDuplicated").value(true));
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.UserQueryIntegrationTest#invalidNicknames")
        public void whenNicknameInvalid(String invalidNickname) throws Exception {
            // when
            ResultActions resultActions = requestValidateNickname(invalidNickname);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidNicknames() {
        return Stream.of(
                null,
                "",
                "공백 포함",
                "a".repeat(12)
        );
    }

    @Nested
    @DisplayName("내 정보 상세 조회")
    class GetMyDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 내 정보를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            // when
            ResultActions resultActions = requestGetMyDetailsAuthenticated();

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.email").value(savedUser.getEmail()))
                    .andExpect(jsonPath("$.data.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.singleWorkCount").value(0))
                    .andExpect(jsonPath("$.data.exhibitionCount").value(0))
                    .andExpect(jsonPath("$.data.followerCount").value(0))
                    .andExpect(jsonPath("$.data.followingCount").value(0));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestGetMyDetails();

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 404를 반환한다")
        public void whenUserNotFound() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();
            userCommandPort.deleteAll();

            // when
            ResultActions resultActions = requestGetMyDetailsAuthenticated();

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    private ResultActions requestGetUserDetails(Long userId) throws Exception {
        return mockMvc.perform(get(ApiPath.USER_DATA, userId));
    }

    private ResultActions requestGetUserDetailsAuthenticated(Long userId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.USER_DATA, userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestValidateNickname(String nickname) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.USER_NICKNAME_EXISTS);
        if (nickname != null) {
            builder = builder.param("nickname", nickname);
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestGetMyDetails() throws Exception {
        return mockMvc.perform(get(ApiPath.USER_MY_DATA));
    }

    private ResultActions requestGetMyDetailsAuthenticated() throws Exception {
        return mockMvc.perform(
                get(ApiPath.USER_MY_DATA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
