package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentUpdateRequest;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCommentCreateRequestFixture;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCommentUpdateRequestFixture;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommentCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkCommentQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.entity.SingleWorkComment;
import com.benchpress200.photique.singlework.domain.support.SingleWorkCommentFixture;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.singlework.infrastructure.persistence.jpa.SingleWorkCommentRepository;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("단일작품 댓글 커맨드 API 통합 테스트")
public class SingleWorkCommentCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private SingleWorkCommandPort singleWorkCommandPort;

    @Autowired
    private SingleWorkTagCommandPort singleWorkTagCommandPort;

    @Autowired
    private SingleWorkCommentCommandPort singleWorkCommentCommandPort;

    @Autowired
    private SingleWorkCommentRepository singleWorkCommentRepository;

    @Autowired
    private SingleWorkCommentQueryPort singleWorkCommentQueryPort;

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
        singleWorkCommentRepository.deleteAll();
        singleWorkTagCommandPort.deleteAll();
        singleWorkCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("단일작품 댓글 생성")
    class CreateSingleWorkCommentTest {

        @Test
        @DisplayName("요청이 유효하면 댓글을 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateSingleWorkCommentAuthenticated(savedSingleWork.getId(), request);
            long commentCount = singleWorkCommentQueryPort.findBySingleWorkIdAndDeletedAtIsNull(
                    savedSingleWork.getId(),
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
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateSingleWorkComment(savedSingleWork.getId(), request);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 단일작품이면 404를 반환한다")
        public void whenSingleWorkNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestCreateSingleWorkCommentAuthenticated(nonExistentId, request);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @ParameterizedTest
        @DisplayName("댓글 내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.singlework.SingleWorkCommentCommandIntegrationTest#invalidContents")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder()
                    .content(invalidContent)
                    .build();

            // when
            ResultActions resultActions = requestCreateSingleWorkCommentAuthenticated(savedSingleWork.getId(), request);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("단일작품 댓글 수정")
    class UpdateSingleWorkCommentTest {

        @Test
        @DisplayName("요청이 유효하면 댓글을 수정하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(savedUser)
                            .singleWork(savedSingleWork)
                            .build()
            );
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkCommentAuthenticated(savedComment.getId(), request);
            Optional<SingleWorkComment> updatedComment = singleWorkCommentQueryPort.findByIdAndDeletedAtIsNull(savedComment.getId());

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(updatedComment)
                    .isPresent()
                    .get()
                    .satisfies(c -> Assertions.assertThat(c.getContent()).isEqualTo(request.getContent()));
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
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(savedUser)
                            .singleWork(savedSingleWork)
                            .build()
            );
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkComment(savedComment.getId(), request);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 댓글이면 404를 반환한다")
        public void whenCommentNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkCommentAuthenticated(nonExistentId, request);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("본인 소유가 아닌 댓글이면 403을 반환한다")
        public void whenNotOwned() throws Exception {
            // given
            User otherUser = userCommandPort.save(
                    UserFixture.builder()
                            .email("other@example.com")
                            .nickname("다른유저")
                            .build()
            );
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(otherUser)
                            .build()
            );
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(otherUser)
                            .singleWork(savedSingleWork)
                            .build()
            );
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkCommentAuthenticated(savedComment.getId(), request);

            // then
            resultActions.andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @DisplayName("댓글 내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.singlework.SingleWorkCommentCommandIntegrationTest#invalidContents")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder()
                    .content(invalidContent)
                    .build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkCommentAuthenticated(1L, request);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("단일작품 댓글 삭제")
    class DeleteSingleWorkCommentTest {

        @Test
        @DisplayName("요청이 유효하면 댓글을 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(savedUser)
                            .singleWork(savedSingleWork)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteSingleWorkCommentAuthenticated(savedComment.getId());
            Optional<SingleWorkComment> deletedComment = singleWorkCommentQueryPort.findByIdAndDeletedAtIsNull(savedComment.getId());

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(deletedComment).isEmpty();
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
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(savedUser)
                            .singleWork(savedSingleWork)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteSingleWorkComment(savedComment.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 댓글이면 204를 반환한다")
        public void whenCommentNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestDeleteSingleWorkCommentAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("본인 소유가 아닌 댓글이면 403을 반환한다")
        public void whenNotOwned() throws Exception {
            // given
            User otherUser = userCommandPort.save(
                    UserFixture.builder()
                            .email("other@example.com")
                            .nickname("다른유저")
                            .build()
            );
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(otherUser)
                            .build()
            );
            SingleWorkComment savedComment = singleWorkCommentCommandPort.save(
                    SingleWorkCommentFixture.builder()
                            .writer(otherUser)
                            .singleWork(savedSingleWork)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteSingleWorkCommentAuthenticated(savedComment.getId());

            // then
            resultActions.andExpect(status().isForbidden());
        }
    }

    private static Stream<String> invalidContents() {
        return Stream.of(
                null,              // @NotBlank 위반
                "",               // @NotBlank 위반
                "a".repeat(301)   // @Size max 초과
        );
    }

    private ResultActions requestCreateSingleWorkComment(
            Long singleWorkId,
            SingleWorkCommentCreateRequest request
    ) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_COMMENT, singleWorkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestCreateSingleWorkCommentAuthenticated(
            Long singleWorkId,
            SingleWorkCommentCreateRequest request
    ) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_COMMENT, singleWorkId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateSingleWorkComment(
            Long commentId,
            SingleWorkCommentUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateSingleWorkCommentAuthenticated(
            Long commentId,
            SingleWorkCommentUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestDeleteSingleWorkComment(Long commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
        );
    }

    private ResultActions requestDeleteSingleWorkCommentAuthenticated(Long commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
