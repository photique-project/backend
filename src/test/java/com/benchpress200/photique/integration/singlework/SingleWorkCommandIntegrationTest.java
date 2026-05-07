package com.benchpress200.photique.integration.singlework;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkUpdateRequest;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCreateRequestFixture;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkUpdateRequestFixture;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.singlework.domain.entity.SingleWork;
import com.benchpress200.photique.singlework.domain.support.SingleWorkFixture;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("단일작품 커맨드 API 통합 테스트")
public class SingleWorkCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private SingleWorkTagCommandPort singleWorkTagCommandPort;

    @Autowired
    private SingleWorkQueryPort singleWorkQueryPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @Autowired
    private SingleWorkCommandPort singleWorkCommandPort;

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
        singleWorkTagCommandPort.deleteAll();
        singleWorkCommandPort.deleteAll();
        userCommandPort.deleteAll();
    }

    @Nested
    @DisplayName("단일작품 생성")
    class PostSingleWorkTest {

        @Test
        @DisplayName("요청이 유효하면 단일작품을 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(workCount).isEqualTo(1);
        }

        @Test
        @DisplayName("인증 토큰이 없으면 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestPostSingleWork(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(workCount).isZero();
        }

        @Test
        @DisplayName("이미지 파일이 비어있으면 400을 반환한다")
        public void whenImageEmpty() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();
            MockMultipartFile emptyImage = MultipartFileFixture.builder()
                    .key("image")
                    .fileName("empty.jpg")
                    .contentType("image/jpeg")
                    .content(new byte[0])
                    .build();
            MockMultipartFile jsonPart = MultipartJsonFixture.builder()
                    .key("singlework")
                    .object(request)
                    .objectMapper(objectMapper)
                    .build();

            // when
            ResultActions resultActions = mockMvc.perform(
                    multipart(ApiPath.SINGLEWORK_ROOT)
                            .file(emptyImage)
                            .file(jsonPart)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("제목이 빈 문자열이면 400을 반환한다")
        public void whenTitleBlank() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                    .title("")
                    .build();

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(workCount).isZero();
        }

        @Test
        @DisplayName("유효하지 않은 카테고리이면 400을 반환한다")
        public void whenCategoryInvalid() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                    .category("invalid_category")
                    .build();

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(workCount).isZero();
        }
    }

    @Nested
    @DisplayName("단일작품 수정")
    class UpdateSingleWorkDetailsTest {

        @Test
        @DisplayName("요청이 유효하면 단일작품을 수정하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );
            SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkAuthenticated(savedSingleWork.getId(), request);
            Optional<SingleWork> singleWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(singleWork)
                    .isPresent()
                    .get()
                    .satisfies(w -> Assertions.assertThat(w.getTitle()).isEqualTo(request.getTitle()));
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
            SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWork(savedSingleWork.getId(), request);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 단일작품이면 404를 반환한다")
        public void whenSingleWorkNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;
            SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkAuthenticated(nonExistentId, request);

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("본인 소유가 아닌 단일작품이면 403을 반환한다")
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
            SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkAuthenticated(savedSingleWork.getId(), request);

            // then
            resultActions.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("제목이 최대 길이를 초과하면 400을 반환한다")
        public void whenTitleTooLong() throws Exception {
            // given
            SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                    .title("가".repeat(31))
                    .updateTitle(true)
                    .build();

            // when
            ResultActions resultActions = requestUpdateSingleWorkAuthenticated(1L, request);

            // then
            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("단일작품 삭제")
    class DeleteSingleWorkTest {

        @Test
        @DisplayName("요청이 유효하면 단일작품을 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWork savedSingleWork = singleWorkCommandPort.save(
                    SingleWorkFixture.builder()
                            .writer(savedUser)
                            .build()
            );

            // when
            ResultActions resultActions = requestDeleteSingleWorkAuthenticated(savedSingleWork.getId());
            Optional<SingleWork> deletedWork = singleWorkQueryPort.findByIdAndDeletedAtIsNull(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(deletedWork).isEmpty();
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

            // when
            ResultActions resultActions = requestDeleteSingleWork(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 단일작품이면 204를 반환한다")
        public void whenSingleWorkNotFound() throws Exception {
            // given
            Long nonExistentId = 9999L;

            // when
            ResultActions resultActions = requestDeleteSingleWorkAuthenticated(nonExistentId);

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("본인 소유가 아닌 단일작품이면 403을 반환한다")
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

            // when
            ResultActions resultActions = requestDeleteSingleWorkAuthenticated(savedSingleWork.getId());

            // then
            resultActions.andExpect(status().isForbidden());
        }
    }

    private ResultActions requestDeleteSingleWork(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_DATA, singleWorkId)
        );
    }

    private ResultActions requestDeleteSingleWorkAuthenticated(Long singleWorkId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_DATA, singleWorkId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }

    private ResultActions requestUpdateSingleWork(
            Long singleWorkId,
            SingleWorkUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_DATA, singleWorkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateSingleWorkAuthenticated(
            Long singleWorkId,
            SingleWorkUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_DATA, singleWorkId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestPostSingleWork(SingleWorkCreateRequest request) throws Exception {
        MockMultipartFile imageFile = MultipartFileFixture.builder()
                .key("image")
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .content(new byte[]{1})
                .build();
        MockMultipartFile jsonPart = MultipartJsonFixture.builder()
                .key("singlework")
                .object(request)
                .objectMapper(objectMapper)
                .build();

        return mockMvc.perform(
                multipart(ApiPath.SINGLEWORK_ROOT)
                        .file(imageFile)
                        .file(jsonPart)
        );
    }

    private ResultActions requestPostSingleWorkAuthenticated(SingleWorkCreateRequest request) throws Exception {
        MockMultipartFile imageFile = MultipartFileFixture.builder()
                .key("image")
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .content(new byte[]{1})
                .build();
        MockMultipartFile jsonPart = MultipartJsonFixture.builder()
                .key("singlework")
                .object(request)
                .objectMapper(objectMapper)
                .build();

        return mockMvc.perform(
                multipart(ApiPath.SINGLEWORK_ROOT)
                        .file(imageFile)
                        .file(jsonPart)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        );
    }
}
