package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCommentCreateRequestFixture;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommentCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionCommentQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionFixture;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("전시회 감상평 커맨드 API 통합 테스트")
public class ExhibitionCommentCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private ExhibitionCommentQueryPort exhibitionCommentQueryPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionCommentCommandPort exhibitionCommentCommandPort;

    @MockitoSpyBean
    private OutboxEventPort outboxEventPort;

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
    @DisplayName("전시회 감상평 생성")
    class CreateExhibitionCommentTest {

        @Test
        @DisplayName("요청이 유효하면 감상평을 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(commentCount).isEqualTo(1);
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateExhibitionComment(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(commentCount).isZero();
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(nonExistentId, request);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("감상평 내용이 빈 문자열이면 400을 반환한다")
        public void whenContentBlank() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder()
                    .content("")
                    .build();

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(commentCount).isZero();
        }

        @Test
        @DisplayName("감상평 내용이 300자를 초과하면 400을 반환한다")
        public void whenContentTooLong() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder()
                    .content("a".repeat(301))
                    .build();

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(commentCount).isZero();
        }

        @Test
        @DisplayName("감상평 저장에 실패하면 감상평을 저장하지 않고 500을 반환한다")
        public void whenCommentSaveFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionCommentCommandPort).save(any());

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(commentCount).isZero();
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 감상평을 저장하지 않고 500을 반환한다")
        public void whenOutboxSaveFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestCreateExhibitionCommentAuthenticated(exhibition.getId(), request);
            long commentCount = exhibitionCommentQueryPort.findByExhibitionId(
                    exhibition.getId(),
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(commentCount).isZero();
        }
    }

    private ResultActions requestCreateExhibitionComment(
            Long exhibitionId,
            ExhibitionCommentCreateRequest request
    ) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_COMMENT, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestCreateExhibitionCommentAuthenticated(
            Long exhibitionId,
            ExhibitionCommentCreateRequest request
    ) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_COMMENT, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
