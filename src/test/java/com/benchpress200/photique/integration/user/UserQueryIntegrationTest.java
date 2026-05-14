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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

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

    private ResultActions requestGetUserDetails(Long userId) throws Exception {
        return mockMvc.perform(get(ApiPath.USER_DATA, userId));
    }

    private ResultActions requestGetUserDetailsAuthenticated(Long userId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.USER_DATA, userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
