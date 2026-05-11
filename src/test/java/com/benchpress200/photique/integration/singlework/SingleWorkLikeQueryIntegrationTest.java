package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkLikeCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkLike;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkLikeRepository;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("단일작품 좋아요 쿼리 API 통합 테스트")
public class SingleWorkLikeQueryIntegrationTest extends BaseIntegrationTest {

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
    @DisplayName("좋아요한 단일작품 검색")
    class SearchLikedSingleWorkTest {

        @Test
        @DisplayName("요청이 유효하면 좋아요한 단일작품 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            singleWorkLikeCommandPort.save(SingleWorkLike.of(savedUser, savedSingleWork));

            // when
            ResultActions resultActions = requestSearchLikedSingleWorkAuthenticated(null, 0, 10);

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(10))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.isFirst").value(true))
                    .andExpect(jsonPath("$.data.isLast").value(true))
                    .andExpect(jsonPath("$.data.hasNext").value(false))
                    .andExpect(jsonPath("$.data.hasPrevious").value(false))
                    .andExpect(jsonPath("$.data.singleWorks.length()").value(1))
                    .andExpect(jsonPath("$.data.singleWorks[0].id").value(savedSingleWork.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.singleWorks[0].writer.introduction").value(savedUser.getIntroduction()))
                    .andExpect(jsonPath("$.data.singleWorks[0].likeCount").value(savedSingleWork.getLikeCount()))
                    .andExpect(jsonPath("$.data.singleWorks[0].isLiked").value(true));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedSingleWork(null, 0, 10);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("키워드 길이가 2 미만이면 400을 반환한다")
        public void whenKeywordTooShort() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedSingleWorkAuthenticated("가", 0, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedSingleWorkAuthenticated(null, -1, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedSingleWorkAuthenticated(null, 0, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private ResultActions requestSearchLikedSingleWork(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_LIKE);
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

    private ResultActions requestSearchLikedSingleWorkAuthenticated(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_LIKE)
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
}
