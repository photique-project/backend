package com.benchpress200.photique.integration.exhibition;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
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

@DisplayName("전시회 좋아요 쿼리 API 통합 테스트")
public class ExhibitionLikeQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private ExhibitionCommandPort exhibitionCommandPort;

    @Autowired
    private ExhibitionLikeCommandPort exhibitionLikeCommandPort;

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
        exhibitionLikeCommandPort.deleteAll();
        exhibitionCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("좋아요한 전시회 검색")
    class SearchLikedExhibitionTest {

        @Test
        @DisplayName("요청이 유효하면 좋아요한 전시회 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionLikeCommandPort.save(ExhibitionLike.of(savedUser, savedExhibition));

            // when
            ResultActions resultActions = requestSearchLikedExhibitionAuthenticated(null, 0, 10);

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
                    .andExpect(jsonPath("$.data.exhibitions.length()").value(1))
                    .andExpect(jsonPath("$.data.exhibitions[0].id").value(savedExhibition.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.exhibitions[0].writer.introduction").value(savedUser.getIntroduction()))
                    .andExpect(jsonPath("$.data.exhibitions[0].title").value(savedExhibition.getTitle()))
                    .andExpect(jsonPath("$.data.exhibitions[0].description").value(savedExhibition.getDescription()))
                    .andExpect(jsonPath("$.data.exhibitions[0].cardColor").value(savedExhibition.getCardColor()))
                    .andExpect(jsonPath("$.data.exhibitions[0].likeCount").value(savedExhibition.getLikeCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].viewCount").value(savedExhibition.getViewCount()))
                    .andExpect(jsonPath("$.data.exhibitions[0].isLiked").value(true))
                    .andExpect(jsonPath("$.data.exhibitions[0].isBookmarked").value(false));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedExhibition(null, 0, 10);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("키워드 길이가 2 미만이면 400을 반환한다")
        public void whenKeywordTooShort() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedExhibitionAuthenticated("가", 0, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedExhibitionAuthenticated(null, -1, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // when
            ResultActions resultActions = requestSearchLikedExhibitionAuthenticated(null, 0, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    private ResultActions requestSearchLikedExhibition(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_LIKE);
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

    private ResultActions requestSearchLikedExhibitionAuthenticated(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_LIKE)
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
