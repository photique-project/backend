package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionCommentFixture;
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

@DisplayName("전시회 감상평 쿼리 API 통합 테스트")
public class ExhibitionCommentQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionCommentCommandPort exhibitionCommentCommandPort;

    @MockitoSpyBean
    private ExhibitionCommentQueryPort exhibitionCommentQueryPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        exhibitionCommentCommandPort.deleteAll();
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
    @DisplayName("전시회 감상평 페이지 조회")
    class GetExhibitionCommentsTest {

        @Test
        @DisplayName("요청이 유효하면 감상평 목록을 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(savedExhibition)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionCommentsAuthenticated(savedExhibition.getId(), 0, 10);

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
                    .andExpect(jsonPath("$.data.comments.length()").value(1))
                    .andExpect(jsonPath("$.data.comments[0].id").value(savedComment.getId()))
                    .andExpect(jsonPath("$.data.comments[0].writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.comments[0].writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.comments[0].writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.comments[0].content").value(savedComment.getContent()));
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionComments(savedExhibition.getId(), 0, 10);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageNegative() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionCommentsAuthenticated(savedExhibition.getId(), -1, 10);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 사이즈가 유효 범위를 벗어나면 400을 반환한다")
        public void whenSizeOutOfRange() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionCommentsAuthenticated(savedExhibition.getId(), 0, 0);

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("감상평 조회 중 DB 예외가 발생하면 500을 반환한다")
        public void whenQueryFails() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionCommentQueryPort).findByExhibitionId(any(), any());

            // when
            ResultActions resultActions = requestGetExhibitionCommentsAuthenticated(savedExhibition.getId(), 0, 10);

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private ResultActions requestGetExhibitionComments(
            Long exhibitionId,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_COMMENT, exhibitionId);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }

    private ResultActions requestGetExhibitionCommentsAuthenticated(
            Long exhibitionId,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.EXHIBITION_COMMENT, exhibitionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }
        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }
        return mockMvc.perform(builder);
    }
}
