package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@DisplayName("전시회 북마크 커맨드 API 통합 테스트")
public class ExhibitionBookmarkCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private ExhibitionBookmarkQueryPort exhibitionBookmarkQueryPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionBookmarkCommandPort exhibitionBookmarkCommandPort;

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
    @DisplayName("전시회 북마크 추가")
    class AddExhibitionBookmarkTest {

        @Test
        @DisplayName("요청이 유효하면 북마크를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestAddExhibitionBookmarkAuthenticated(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(exists).isTrue();
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
            ResultActions resultActions = requestAddExhibitionBookmark(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
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
            ResultActions resultActions = requestAddExhibitionBookmarkAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("이미 북마크한 전시회이면 북마크를 추가하지 않고 409를 반환한다")
        public void whenAlreadyBookmarked() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, exhibition));

            // when
            ResultActions resultActions = requestAddExhibitionBookmarkAuthenticated(exhibition.getId());

            // then
            resultActions.andExpect(status().isConflict());
        }

        @Test
        @DisplayName("북마크 저장에 실패하면 북마크를 저장하지 않고 500을 반환한다")
        public void whenBookmarkSaveFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionBookmarkCommandPort).save(any());

            // when
            ResultActions resultActions = requestAddExhibitionBookmarkAuthenticated(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("전시회 북마크 취소")
    class CancelExhibitionBookmarkTest {

        @Test
        @DisplayName("요청이 유효하면 북마크를 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, exhibition));

            // when
            ResultActions resultActions = requestCancelExhibitionBookmarkAuthenticated(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

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
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, exhibition));

            // when
            ResultActions resultActions = requestCancelExhibitionBookmark(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 전시회이면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestCancelExhibitionBookmarkAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("북마크가 없는 전시회이면 204를 반환한다")
        public void whenBookmarkNotExists() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestCancelExhibitionBookmarkAuthenticated(exhibition.getId());

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("북마크 삭제 중 DB 예외가 발생하면 500을 반환한다")
        public void whenBookmarkDeleteFails() throws Exception {
            // given
            Exhibition exhibition = exhibitionCommandPort.save(
                    ExhibitionFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            exhibitionBookmarkCommandPort.save(ExhibitionBookmark.of(savedUser, exhibition));
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionBookmarkCommandPort).delete(any());

            // when
            ResultActions resultActions = requestCancelExhibitionBookmarkAuthenticated(exhibition.getId());
            boolean exists = exhibitionBookmarkQueryPort.existsByUserIdAndExhibitionId(
                    savedUser.getId(),
                    exhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exists).isTrue();
        }
    }

    private ResultActions requestAddExhibitionBookmark(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
        );
    }

    private ResultActions requestAddExhibitionBookmarkAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestCancelExhibitionBookmark(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
        );
    }

    private ResultActions requestCancelExhibitionBookmarkAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
