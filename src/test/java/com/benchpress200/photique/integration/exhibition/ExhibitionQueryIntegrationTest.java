package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("전시회 쿼리 API 통합 테스트")
public class ExhibitionQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionQueryPort exhibitionQueryPort;

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
        exhibitionCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("전시회 상세 조회")
    class GetExhibitionDetailsTest {

        @Test
        @DisplayName("요청이 유효하면 전시회 상세 정보를 반환하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestGetExhibitionDetailsAuthenticated(savedExhibition.getId());

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(savedExhibition.getId()))
                    .andExpect(jsonPath("$.data.writer.id").value(savedUser.getId()))
                    .andExpect(jsonPath("$.data.writer.nickname").value(savedUser.getNickname()))
                    .andExpect(jsonPath("$.data.writer.profileImage").value(savedUser.getProfileImage()))
                    .andExpect(jsonPath("$.data.writer.introduction").value(savedUser.getIntroduction()))
                    .andExpect(jsonPath("$.data.title").value(savedExhibition.getTitle()))
                    .andExpect(jsonPath("$.data.description").value(savedExhibition.getDescription()))
                    .andExpect(jsonPath("$.data.tags.length()").value(0))
                    .andExpect(jsonPath("$.data.works.length()").value(0))
                    .andExpect(jsonPath("$.data.viewCount").value(savedExhibition.getViewCount()))
                    .andExpect(jsonPath("$.data.likeCount").value(savedExhibition.getLikeCount()))
                    .andExpect(jsonPath("$.data.createdAt").exists())
                    .andExpect(jsonPath("$.data.isFollowing").value(false))
                    .andExpect(jsonPath("$.data.isLiked").value(false))
                    .andExpect(jsonPath("$.data.isBookmarked").value(false));
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
            ResultActions resultActions = requestGetExhibitionDetails(savedExhibition.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestGetExhibitionDetailsAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("전시회 조회 중 DB 예외가 발생하면 500을 반환한다")
        public void whenQueryFails() throws Exception {
            // given
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionQueryPort).findByIdAndDeletedAtIsNull(any());

            // when
            ResultActions resultActions = requestGetExhibitionDetailsAuthenticated(1L);

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private ResultActions requestGetExhibitionDetails(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
        );
    }

    private ResultActions requestGetExhibitionDetailsAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
