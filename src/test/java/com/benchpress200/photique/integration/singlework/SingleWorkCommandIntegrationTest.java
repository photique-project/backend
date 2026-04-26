package com.benchpress200.photique.integration.singlework;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCreateRequestFixture;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkCommandPort;
import com.benchpress200.photique.singlework.application.command.port.out.persistence.SingleWorkTagCommandPort;
import com.benchpress200.photique.singlework.application.query.port.out.persistence.SingleWorkQueryPort;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
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

    @MockitoSpyBean
    private ImageUploaderPort imageUploaderPort;

    @MockitoSpyBean
    private SingleWorkCommandPort singleWorkCommandPort;

    @MockitoSpyBean
    private OutboxEventPort outboxEventPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        singleWorkTagCommandPort.deleteAll();
        singleWorkCommandPort.deleteAll();
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

        @Test
        @DisplayName("이미지 업로드에 실패하면 단일작품을 저장하지 않고 500을 반환한다")
        public void whenImageUploadFails() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();
            doThrow(new DataAccessResourceFailureException("S3 에러"))
                    .when(imageUploaderPort).upload(any(), any());

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(workCount).isZero();
        }

        @Test
        @DisplayName("단일작품 저장에 실패하면 단일작품을 저장하지 않고 500을 반환한다")
        public void whenSingleWorkSaveFails() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();
            doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(singleWorkCommandPort).save(any());

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(workCount).isZero();
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 단일작품을 저장하지 않고 500을 반환한다")
        public void whenOutboxSaveFails() throws Exception {
            // given
            SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build();
            doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestPostSingleWorkAuthenticated(request);
            long workCount = singleWorkQueryPort.searchMySingleWorkByDeletedAtIsNull(
                    savedUser.getId(),
                    "",
                    Pageable.unpaged()
            ).getTotalElements();

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(workCount).isZero();
        }
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
