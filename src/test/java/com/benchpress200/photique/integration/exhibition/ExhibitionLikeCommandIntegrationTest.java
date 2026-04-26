package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionLikeCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionLikeQueryPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionLike;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("전시회 좋아요 커맨드 API 통합 테스트")
public class ExhibitionLikeCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private ExhibitionLikeQueryPort exhibitionLikeQueryPort;

    @Autowired
    private ExhibitionQueryPort exhibitionQueryPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionLikeCommandPort exhibitionLikeCommandPort;

    @MockitoSpyBean
    private OutboxEventPort outboxEventPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        exhibitionLikeCommandPort.deleteAll();
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
    @DisplayName("전시회 좋아요 추가")
    class AddExhibitionLikeTest {

        @Test
        @DisplayName("요청이 유효하면 좋아요를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(exhibition.getId());
            boolean exists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );
            long likeCount = exhibitionQueryPort.findByIdAndDeletedAtIsNull(exhibition.getId())
                    .orElseThrow()
                    .getLikeCount();

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(exists).isTrue();
            Assertions.assertThat(likeCount).isEqualTo(1);
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

            // when
            ResultActions resultActions = requestAddExhibitionLike(exhibition.getId());
            boolean exists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 좋아요한 전시회이면 409를 반환한다")
        public void whenAlreadyLiked() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionLikeCommandPort.save(ExhibitionLike.of(savedUser, exhibition));

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(exhibition.getId());

            // then
            resultActions.andExpect(status().isConflict());
        }

        @Test
        @DisplayName("좋아요 저장에 실패하면 좋아요를 저장하지 않고 500을 반환한다")
        public void whenLikeSaveFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionLikeCommandPort).save(any());

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(exhibition.getId());
            boolean exists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("좋아요 수 증가에 실패하면 좋아요를 저장하지 않고 500을 반환한다")
        public void whenIncrementLikeCountFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionCommandPort).incrementLikeCount(any());

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(exhibition.getId());
            boolean exists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 좋아요를 저장하지 않고 500을 반환한다")
        public void whenOutboxSaveFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestAddExhibitionLikeAuthenticated(exhibition.getId());
            boolean exists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("전시회 좋아요 취소")
    class CancelExhibitionLikeTest {

        @Test
        @DisplayName("요청이 유효하면 좋아요를 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            requestAddExhibitionLikeAuthenticated(savedExhibition.getId());

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(savedExhibition.getId());
            boolean likeExists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    savedExhibition.getId()
            );
            long likeCount = exhibitionQueryPort.findByIdAndDeletedAtIsNull(savedExhibition.getId())
                    .orElseThrow()
                    .getLikeCount();

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(likeExists).isFalse();
            Assertions.assertThat(likeCount).isZero();
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
            ResultActions resultActions = requestCancelExhibitionLike(savedExhibition.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("좋아요하지 않은 전시회이면 아무 처리 없이 204를 반환한다")
        public void whenNotLiked() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(savedExhibition.getId());

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("좋아요 삭제에 실패하면 롤백하고 500을 반환한다")
        public void whenLikeDeleteFails() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            requestAddExhibitionLikeAuthenticated(savedExhibition.getId());
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionLikeCommandPort).delete(any());

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(savedExhibition.getId());
            boolean likeExists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(likeExists).isTrue();
        }

        @Test
        @DisplayName("좋아요 수 감소에 실패하면 롤백하고 500을 반환한다")
        public void whenDecrementLikeCountFails() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            requestAddExhibitionLikeAuthenticated(savedExhibition.getId());
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionCommandPort).decrementLikeCount(any());

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(savedExhibition.getId());
            boolean likeExists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(likeExists).isTrue();
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 롤백하고 500을 반환한다")
        public void whenOutboxSaveFails() throws Exception {
            // given
            Exhibition savedExhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            requestAddExhibitionLikeAuthenticated(savedExhibition.getId());
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestCancelExhibitionLikeAuthenticated(savedExhibition.getId());
            boolean likeExists = exhibitionLikeQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(likeExists).isTrue();
        }
    }

    private ResultActions requestAddExhibitionLike(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_LIKE, exhibitionId)
        );
    }

    private ResultActions requestAddExhibitionLikeAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_LIKE, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestCancelExhibitionLike(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_LIKE, exhibitionId)
        );
    }

    private ResultActions requestCancelExhibitionLikeAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_LIKE, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
