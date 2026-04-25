package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentUpdateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCommentCreateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCommentUpdateRequestFixture;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionComment;
import com.benchpress200.photique.exhibition.domain.support.ExhibitionCommentFixture;
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

    @Nested
    @DisplayName("전시회 감상평 수정")
    class UpdateExhibitionCommentTest {

        @Test
        @DisplayName("요청이 유효하면 감상평을 수정하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content("수정된 감상평")
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionCommentAuthenticated(savedComment.getId(), request);
            ExhibitionComment updatedComment = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(updatedComment.getContent()).isEqualTo("수정된 감상평");
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
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content("수정된 감상평")
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionComment(savedComment.getId(), request);
            ExhibitionComment comment = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(comment.getContent()).isEqualTo(savedComment.getContent());
        }

        @Test
        @DisplayName("존재하지 않는 감상평이면 404를 반환한다")
        public void whenCommentNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateExhibitionCommentAuthenticated(nonExistentId, request);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("감상평 작성자가 아니면 403을 반환한다")
        public void whenNotOwner() throws Exception {
            // given
            User otherUser = userCommandPort.save(
                    UserFixture.builder()
                            .email("other@example.com")
                            .nickname("다른유저")
                            .build()
            );
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(otherUser)
                            .exhibition(exhibition)
                            .build()
            );
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content("수정된 감상평")
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionCommentAuthenticated(savedComment.getId(), request);
            ExhibitionComment comment = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isForbidden());
            Assertions.assertThat(comment.getContent()).isEqualTo(savedComment.getContent());
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
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content("")
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionCommentAuthenticated(savedComment.getId(), request);
            ExhibitionComment comment = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(comment.getContent()).isEqualTo(savedComment.getContent());
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
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content("a".repeat(301))
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionCommentAuthenticated(savedComment.getId(), request);
            ExhibitionComment comment = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .orElseThrow();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(comment.getContent()).isEqualTo(savedComment.getContent());
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

    @Nested
    @DisplayName("전시회 감상평 삭제")
    class DeleteExhibitionCommentTest {

        @Test
        @DisplayName("요청이 유효하면 감상평을 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteExhibitionCommentAuthenticated(savedComment.getId());
            boolean exists = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .isPresent();

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(exists).isFalse();
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
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(savedUser)
                            .exhibition(exhibition)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteExhibitionComment(savedComment.getId());
            boolean exists = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .isPresent();

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 감상평이면 204를 반환한다")
        public void whenCommentNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestDeleteExhibitionCommentAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("감상평 작성자가 아니면 403을 반환한다")
        public void whenNotOwner() throws Exception {
            // given
            User otherUser = userCommandPort.save(
                    UserFixture.builder()
                            .email("other@example.com")
                            .nickname("다른유저")
                            .build()
            );
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            ExhibitionComment savedComment = exhibitionCommentCommandPort.save(
                    ExhibitionCommentFixture.builder()
                            .writer(otherUser)
                            .exhibition(exhibition)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteExhibitionCommentAuthenticated(savedComment.getId());
            boolean exists = exhibitionCommentQueryPort
                    .findByIdAndDeletedAtIsNull(savedComment.getId())
                    .isPresent();

            // then
            resultActions.andExpect(status().isForbidden());
            Assertions.assertThat(exists).isTrue();
        }
    }

    private ResultActions requestUpdateExhibitionComment(
            Long commentId,
            ExhibitionCommentUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateExhibitionCommentAuthenticated(
            Long commentId,
            ExhibitionCommentUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestDeleteExhibitionComment(Long commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
        );
    }

    private ResultActions requestDeleteExhibitionCommentAuthenticated(Long commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
