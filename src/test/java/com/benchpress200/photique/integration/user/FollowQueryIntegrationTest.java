package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
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

@DisplayName("팔로우 쿼리 API 통합 테스트")
public class FollowQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    private User savedUser;
    private String accessToken;

    @AfterEach
    void cleanUp() {
        followRepository.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("팔로이 검색")
    class SearchFolloweeTest {
        @Test
        @DisplayName("요청이 유효하면 팔로이 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            User targetUser = userCommandPort.save(UserFixture.builder()
                    .email("target@example.com")
                    .nickname("타겟유저")
                    .build());
            followRepository.save(Follow.of(savedUser, targetUser));
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            String keyword = "타겟";
            int page = 0;
            int size = 30;

            // when
            ResultActions resultActions = requestSearchFollowee(savedUser.getId(), keyword, page, size);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.users[0].nickname").value(targetUser.getNickname()))
                    .andExpect(jsonPath("$.data.users[0].isFollowing").value(true));
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환하고 200을 반환한다")
        public void whenSearchResultEmpty() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            String keyword = "없는키워드";
            int page = 0;
            int size = 30;

            // when
            ResultActions resultActions = requestSearchFollowee(savedUser.getId(), keyword, page, size);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            long userId = 1L;

            // when
            ResultActions resultActions = requestSearchFolloweeUnauthenticated(userId);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            int page = -1;
            int size = 30;

            // when
            ResultActions resultActions = requestSearchFollowee(savedUser.getId(), null, page, size);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 크기가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.user.FollowQueryIntegrationTest#invalidSizes")
        public void whenSizeInvalid(Integer invalidSize) throws Exception {
            // given
            savedUser = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId(),
                    savedUser.getRole().name()
            ).getAccessToken();

            int page = 0;

            // when
            ResultActions resultActions = requestSearchFollowee(savedUser.getId(), null, page, invalidSize);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private static Stream<Integer> invalidSizes() {
        return Stream.of(0, 51);
    }

    private ResultActions requestSearchFollowee(
            Long userId,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.FOLLOWEE, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchFolloweeUnauthenticated(Long userId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.FOLLOWEE, userId)
                        .param("keyword", "테스트")
        );
    }
}
