package com.benchpress200.photique.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.FollowQueryPort;
import com.benchpress200.photique.user.domain.entity.Follow;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import com.benchpress200.photique.user.infrastructure.persistence.jpa.FollowRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("팔로우 커맨드 API 통합 테스트")
public class FollowCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private FollowQueryPort followQueryPort;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    private User savedFollower;
    private User savedFollowee;
    private String accessToken;

    @AfterEach
    void cleanUp() {
        followRepository.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("팔로우")
    class FollowTest {
        @Test
        @DisplayName("요청이 유효하면 팔로우를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            savedFollowee = userCommandPort.save(UserFixture.builder()
                    .email("followee@example.com")
                    .nickname("팔로이유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();

            // when
            ResultActions resultActions = requestFollowAuthenticated(savedFollowee.getId());
            Optional<Follow> follow = followQueryPort.findByFollowerIdAndFolloweeId(
                    savedFollower.getId(),
                    savedFollowee.getId()
            );

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(follow).isPresent();
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            savedFollowee = userCommandPort.save(UserFixture.builder().build());

            // when
            ResultActions resultActions = requestFollow(savedFollowee.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("본인을 팔로우하면 400을 반환한다")
        public void whenSelfFollow() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();

            // when
            ResultActions resultActions = requestFollowAuthenticated(savedFollower.getId());

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("이미 팔로우한 상태이면 204를 반환한다")
        public void whenAlreadyFollowed() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            savedFollowee = userCommandPort.save(UserFixture.builder()
                    .email("followee@example.com")
                    .nickname("팔로이유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();
            requestFollowAuthenticated(savedFollowee.getId());

            // when
            ResultActions resultActions = requestFollowAuthenticated(savedFollowee.getId());

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("팔로워 유저가 존재하지 않으면 404를 반환한다")
        public void whenFollowerNotFound() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            savedFollowee = userCommandPort.save(UserFixture.builder()
                    .email("followee@example.com")
                    .nickname("팔로이유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();
            followRepository.deleteAll();
            userCommandPort.deleteAll();

            // when
            ResultActions resultActions = requestFollowAuthenticated(savedFollowee.getId());

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("팔로이 유저가 존재하지 않으면 404를 반환한다")
        public void whenFolloweeNotFound() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            savedFollowee = userCommandPort.save(UserFixture.builder()
                    .email("followee@example.com")
                    .nickname("팔로이유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();
            Long nonExistentFolloweeId = savedFollowee.getId() + 9999L;

            // when
            ResultActions resultActions = requestFollowAuthenticated(nonExistentFolloweeId);

            // then
            resultActions.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("언팔로우")
    class UnfollowTest {
        @Test
        @DisplayName("요청이 유효하면 팔로우를 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            savedFollowee = userCommandPort.save(UserFixture.builder()
                    .email("followee@example.com")
                    .nickname("팔로이유저")
                    .build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();
            followRepository.save(Follow.of(savedFollower, savedFollowee));

            // when
            ResultActions resultActions = requestUnfollowAuthenticated(savedFollowee.getId());
            Optional<Follow> follow = followQueryPort.findByFollowerIdAndFolloweeId(
                    savedFollower.getId(),
                    savedFollowee.getId()
            );

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(follow).isNotPresent();
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            savedFollowee = userCommandPort.save(UserFixture.builder().build());

            // when
            ResultActions resultActions = requestUnfollow(savedFollowee.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("본인을 언팔로우하면 400을 반환한다")
        public void whenSelfUnfollow() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();

            // when
            ResultActions resultActions = requestUnfollowAuthenticated(savedFollower.getId());

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("팔로우 관계가 없어도 204를 반환한다")
        public void whenFollowNotFound() throws Exception {
            // given
            savedFollower = userCommandPort.save(UserFixture.builder().build());
            accessToken = authenticationTokenManagerPort.issueTokens(
                    savedFollower.getId(),
                    savedFollower.getRole().name()
            ).getAccessToken();
            Long nonExistentFolloweeId = savedFollower.getId() + 9999L;

            // when
            ResultActions resultActions = requestUnfollowAuthenticated(nonExistentFolloweeId);

            // then
            resultActions.andExpect(status().isNoContent());
        }
    }

    private ResultActions requestFollow(Long followeeId) throws Exception {
        return mockMvc.perform(post(ApiPath.FOLLOW_ROOT, followeeId));
    }

    private ResultActions requestFollowAuthenticated(Long followeeId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.FOLLOW_ROOT, followeeId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestUnfollow(Long followeeId) throws Exception {
        return mockMvc.perform(delete(ApiPath.FOLLOW_ROOT, followeeId));
    }

    private ResultActions requestUnfollowAuthenticated(Long followeeId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.FOLLOW_ROOT, followeeId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
