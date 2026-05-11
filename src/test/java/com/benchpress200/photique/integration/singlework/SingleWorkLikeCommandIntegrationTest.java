package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkLikeQueryPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("단일작품 좋아요 커맨드 API 통합 테스트")
public class SingleWorkLikeCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private SingleWorkCommandPort singleWorkCommandPort;

    @Autowired
    private SingleWorkTagCommandPort singleWorkTagCommandPort;

    @Autowired
    private SingleWorkLikeCommandPort singleWorkLikeCommandPort;

    @Autowired
    private SingleWorkLikeQueryPort singleWorkLikeQueryPort;

    @Autowired
    private SingleWorkQueryPort singleWorkQueryPort;

    @Autowired
    private SingleWorkLikeRepository singleWorkLikeRepository;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        User user = UserFixture.builder().build();
        savedUser = userCommandPort.save(user);

        AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                savedUser.getId(),
                savedUser.getRole().name()
        );
        accessToken = tokens.getAccessToken();
    }

    @AfterEach
    void cleanUp() {
        singleWorkLikeRepository.deleteAll();
        singleWorkTagCommandPort.deleteAll();
        singleWorkCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("단일작품 좋아요 추가")
    class AddSingleWorkLikeTest {

        @Test
        @DisplayName("요청이 유효하면 좋아요를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestAddSingleWorkLikeAuthenticated(savedSingleWork.getId());
            boolean likeExists = singleWorkLikeQueryPort.existsByUserIdAndSingleWorkId(
                    savedUser.getId(),
                    savedSingleWork.getId()
            );
            SingleWork updatedSingleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(savedSingleWork.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(likeExists).isTrue();
            Assertions.assertThat(updatedSingleWork.getLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestAddSingleWorkLike(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 단일작품이면 404를 반환한다")
        public void whenSingleWorkNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestAddSingleWorkLikeAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 좋아요한 단일작품이면 409를 반환한다")
        public void whenAlreadyLiked() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            singleWorkLikeCommandPort.save(SingleWorkLike.of(savedUser, savedSingleWork));

            // when
            ResultActions resultActions = requestAddSingleWorkLikeAuthenticated(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isConflict());
        }
    }

    private ResultActions requestAddSingleWorkLike(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_LIKE, singleWorkId)
        );
    }

    private ResultActions requestAddSingleWorkLikeAuthenticated(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_LIKE, singleWorkId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
