package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionBookmarkCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionBookmarkQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionBookmark;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DisplayName("전시회 북마크 쿼리 API 통합 테스트")
public class ExhibitionBookmarkQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionBookmarkCommandPort exhibitionBookmarkCommandPort;

    @MockitoSpyBean
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        exhibitionBookmarkCommandPort.deleteAll();
        exhibitionCommandPort.deleteAll();
        userCommandPort.deleteAll();

        User user = UserFixture.builder().build();
        savedUser = userCommandPort.save(user);

        AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                savedUser.getId(),
                savedUser.getRole().name()
        );
        accessToken = tokens.getAccessToken();
    }

    @Nested
    @DisplayName("북마크 전시회 검색")
    class SearchBookmarkedExhibitionTest {

        @Test
        @DisplayName("요청이 유효하면 북마크 전시회 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, savedExhibition));

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibitionAuthenticated(null, 0, 10);

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
                    .andExpect(jsonPath("$.data.exhibitions[0].isLiked").value(false))
                    .andExpect(jsonPath("$.data.exhibitions[0].isBookmarked").value(true));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestSearchBookmarkedExhibition(null, 0, 10);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("키워드 길이가 2 미만이면 400을 반환한다")
        public void whenKeywordTooShort() throws Exception {
            // when
            ResultActions resultActions = requestSearchBookmarkedExhibitionAuthenticated("가", 0, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // when
            ResultActions resultActions = requestSearchBookmarkedExhibitionAuthenticated(null, -1, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // when
            ResultActions resultActions = requestSearchBookmarkedExhibitionAuthenticated(null, 0, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("북마크 조회 중 DB 예외가 발생하면 500을 반환한다")
        public void whenQueryFails() throws Exception {
            // given
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionBookmarkQueryPort).searchBookmarkedExhibitionByDeletedAtIsNull(any(), any(), any());

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibitionAuthenticated(null, 0, 10);

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private ResultActions requestSearchBookmarkedExhibition(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_BOOKMARK);
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

    private ResultActions requestSearchBookmarkedExhibitionAuthenticated(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_MY_BOOKMARK)
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
