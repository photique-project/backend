package com.benchpress200.photique.integration.exhibition;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.auth.application.command.port.out.security.AuthenticationTokenManagerPort;
import com.benchpress200.photique.auth.domain.vo.AuthenticationTokens;
import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCreateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionUpdateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionUpdateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionWorkCreateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionWorkUpdateRequestFixture;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionTagCommandPort;
import com.benchpress200.photique.exhibition.application.command.port.out.ExhibitionWorkCommandPort;
import com.benchpress200.photique.exhibition.application.query.port.out.persistence.ExhibitionQueryPort;
import com.benchpress200.photique.exhibition.domain.entity.Exhibition;
import com.benchpress200.photique.exhibition.domain.entity.ExhibitionWork;
import com.benchpress200.photique.image.domain.port.storage.ImageUploaderPort;
import com.benchpress200.photique.image.infrastructure.exception.ImageUploadException;
import com.benchpress200.photique.outbox.application.port.out.persistence.OutboxEventPort;
import com.benchpress200.photique.support.base.BaseIntegrationTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import com.benchpress200.photique.user.application.command.port.out.persistence.UserCommandPort;
import com.benchpress200.photique.user.application.query.port.out.persistence.UserQueryPort;
import com.benchpress200.photique.user.domain.entity.User;
import com.benchpress200.photique.user.domain.support.UserFixture;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@DisplayName("전시회 커맨드 API 통합 테스트")
public class ExhibitionCommandIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserQueryPort userQueryPort;

    @Autowired
    private UserCommandPort userCommandPort;

    @Autowired
    private ExhibitionQueryPort exhibitionQueryPort;

    @Autowired
    private AuthenticationTokenManagerPort authenticationTokenManagerPort;

    @MockitoSpyBean
    private ImageUploaderPort imageUploaderPort;

    @MockitoSpyBean
    private ExhibitionCommandPort exhibitionCommandPort;

    @MockitoSpyBean
    private ExhibitionWorkCommandPort exhibitionWorkCommandPort;

    @MockitoSpyBean
    private ExhibitionTagCommandPort exhibitionTagCommandPort;

    @MockitoSpyBean
    private OutboxEventPort outboxEventPort;

    private User savedUser;
    private String accessToken;

    @BeforeEach
    void setUp() {
        exhibitionTagCommandPort.deleteAll();
        exhibitionWorkCommandPort.deleteAll();
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
    @DisplayName("전시회 생성")
    class OpenExhibitionTest {
        @Test
        @DisplayName("요청이 유효하면 전시회를 저장하고 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isCreated());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(1L);
        }

        @ParameterizedTest
        @DisplayName("제목이 유효하지 않으면 전시회를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidTitles")
        public void whenTitleInvalid(String invalidTitle) throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .title(invalidTitle)
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @ParameterizedTest
        @DisplayName("설명이 유효하지 않으면 전시회를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidDescriptions")
        public void whenDescriptionInvalid(String invalidDescription) throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .description(invalidDescription)
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @ParameterizedTest
        @DisplayName("카드 색상이 유효하지 않으면 전시회를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidCardColors")
        public void whenCardColorInvalid(String invalidCardColor) throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .cardColor(invalidCardColor)
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("작품 목록이 null이면 전시회를 저장하지 않고 400을 반환한다")
        public void whenWorksNull() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .works(null)
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("작품 목록이 비어있으면 전시회를 저장하지 않고 400을 반환한다")
        public void whenWorksEmpty() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .works(List.of())
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @ParameterizedTest
        @DisplayName("이미지가 유효하지 않으면 전시회를 저장하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidImages")
        public void whenImageInvalid(MockMultipartFile invalidImage) throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(invalidImage);

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("작품 순서가 중복되면 전시회를 저장하지 않고 400을 반환한다")
        public void whenDisplayOrderDuplicated() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                    .works(List.of(
                            ExhibitionWorkCreateRequestFixture.builder().displayOrder(0).build(),
                            ExhibitionWorkCreateRequestFixture.builder().displayOrder(0).build()
                    ))
                    .build();
            List<MockMultipartFile> images = List.of(createValidImage(), createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("인증되지 않은 사용자면 전시회를 저장하지 않고 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            // when
            ResultActions resultActions = requestOpenExhibition(request, images);

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("이미지 업로드에 실패하면 전시회를 저장하지 않고 500을 반환한다")
        public void whenImageUploadFails() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            Mockito.doThrow(new ImageUploadException("이미지 업로드 실패"))
                    .when(imageUploaderPort).upload(any(), any());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("존재하지 않는 유저로 요청하면 전시회를 저장하지 않고 404를 반환한다")
        public void whenUserNotFound() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            AuthenticationTokens tokens = authenticationTokenManagerPort.issueTokens(
                    savedUser.getId() + 999L,
                    savedUser.getRole().name()
            );
            String notFoundUserToken = tokens.getAccessToken();

            // when
            ResultActions resultActions = requestOpenExhibitionWithToken(request, images, notFoundUserToken);

            // then
            resultActions.andExpect(status().isNotFound());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("전시회 저장에 실패하면 전시회를 저장하지 않고 500을 반환한다")
        public void whenExhibitionSaveFails() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionCommandPort).save(any());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("작품 저장에 실패하면 500을 반환한다")
        public void whenExhibitionWorkSaveFails() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionWorkCommandPort).save(any());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("전시회 태그 저장에 실패하면 500을 반환한다")
        public void whenExhibitionTagSaveFails() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(exhibitionTagCommandPort).saveAll(any());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 500을 반환한다")
        public void whenOutboxEventSaveFails() throws Exception {
            // given
            ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();
            List<MockMultipartFile> images = List.of(createValidImage());

            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestOpenExhibitionAuthenticated(request, images);

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibitionQueryPort.countByWriter(savedUser)).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("전시회 수정")
    class UpdateExhibitionDetailsTest {
        private Exhibition savedExhibition;
        private ExhibitionWork savedWork;
        private ExhibitionWork savedWork2;

        @BeforeEach
        void setUpExhibition() {
            Exhibition exhibition = Exhibition.builder()
                    .writer(savedUser)
                    .title("원본 전시회 제목")
                    .description("원본 설명")
                    .cardColor("#FFFFFF")
                    .viewCount(0L)
                    .likeCount(0L)
                    .build();
            savedExhibition = exhibitionCommandPort.save(exhibition);

            ExhibitionWork work = ExhibitionWork.builder()
                    .exhibition(savedExhibition)
                    .displayOrder(0)
                    .title("원본 작품 제목")
                    .description("원본 작품 설명")
                    .image("https://test-image.jpg")
                    .build();
            savedWork = exhibitionWorkCommandPort.save(work);

            ExhibitionWork work2 = ExhibitionWork.builder()
                    .exhibition(savedExhibition)
                    .displayOrder(1)
                    .title("원본 작품 제목2")
                    .description("원본 작품 설명2")
                    .image("https://test-image2.jpg")
                    .build();
            savedWork2 = exhibitionWorkCommandPort.save(work2);
        }

        @Test
        @DisplayName("요청이 유효하면 전시회를 수정하고 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> updatedExhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(updatedExhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getTitle()).isEqualTo("수정된 전시회 제목"));
        }

        @Test
        @DisplayName("인증되지 않은 사용자면 전시회를 수정하지 않고 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetails(savedExhibition.getId(), request);

            // then
            resultActions.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("존재하지 않는 전시회면 404를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId() + 999L,
                    request
            );

            // then
            resultActions.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("전시회 소유자가 아니면 전시회를 수정하지 않고 403을 반환한다")
        public void whenNotOwned() throws Exception {
            // given
            User otherUser = UserFixture.builder()
                    .email("other@example.com")
                    .nickname("다른유저")
                    .build();
            User savedOtherUser = userCommandPort.save(otherUser);
            AuthenticationTokens otherTokens = authenticationTokenManagerPort.issueTokens(
                    savedOtherUser.getId(),
                    savedOtherUser.getRole().name()
            );

            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder().build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsWithToken(
                    savedExhibition.getId(),
                    request,
                    otherTokens.getAccessToken()
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isForbidden());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getTitle()).isEqualTo("원본 전시회 제목"));
        }

        @ParameterizedTest
        @DisplayName("제목이 유효하지 않으면 전시회를 수정하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidUpdateTitles")
        public void whenTitleInvalid(String invalidTitle) throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(true)
                    .title(invalidTitle)
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getTitle()).isEqualTo("원본 전시회 제목"));
        }

        @ParameterizedTest
        @DisplayName("설명이 유효하지 않으면 전시회를 수정하지 않고 400을 반환한다")
        @MethodSource("com.benchpress200.photique.integration.exhibition.ExhibitionCommandIntegrationTest#invalidUpdateDescriptions")
        public void whenDescriptionInvalid(String invalidDescription) throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(false)
                    .updateDescription(true)
                    .description(invalidDescription)
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getDescription()).isEqualTo("원본 설명"));
        }

        @Test
        @DisplayName("카드 색상이 유효하지 않으면 전시회를 수정하지 않고 400을 반환한다")
        public void whenCardColorInvalid() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(false)
                    .updateCardColor(true)
                    .cardColor("a".repeat(21))
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getCardColor()).isEqualTo("#FFFFFF"));
        }

        @Test
        @DisplayName("수정 플래그가 true이나 해당 필드가 null이면 전시회를 수정하지 않고 400을 반환한다")
        public void whenUpdateFlagTrueButFieldNull() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(true)
                    .title(null)
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isBadRequest());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getTitle()).isEqualTo("원본 전시회 제목"));
        }

        @Test
        @DisplayName("존재하지 않는 작품을 수정하려 하면 전시회를 수정하지 않고 404를 반환한다")
        public void whenWorkNotFound() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(false)
                    .updateWorks(true)
                    .works(List.of(
                            ExhibitionWorkUpdateRequestFixture.builder()
                                    .id(savedWork.getId() + 999L)
                                    .build()
                    ))
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isNotFound());
            Assertions.assertThat(exhibition)
                    .isPresent()
                    .get()
                    .satisfies(e -> Assertions.assertThat(e.getTitle()).isEqualTo("원본 전시회 제목"));
        }

        @Test
        @DisplayName("작품 순서가 중복되면 전시회를 수정하지 않고 400을 반환한다")
        public void whenWorkDisplayOrderDuplicated() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder()
                    .updateTitle(false)
                    .updateWorks(true)
                    .works(List.of(
                            ExhibitionWorkUpdateRequestFixture.builder()
                                    .id(savedWork.getId())
                                    .displayOrder(0)
                                    .build(),
                            ExhibitionWorkUpdateRequestFixture.builder()
                                    .id(savedWork2.getId())
                                    .displayOrder(0)
                                    .build()
                    ))
                    .build();

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 500을 반환한다")
        public void whenOutboxEventSaveFails() throws Exception {
            // given
            ExhibitionUpdateRequest request = ExhibitionUpdateRequestFixture.builder().build();

            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestUpdateExhibitionDetailsAuthenticated(
                    savedExhibition.getId(),
                    request
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
        }
    }

    private static Stream<String> invalidUpdateTitles() {
        return Stream.of(
                "",                 // @Size min 위반
                "a".repeat(31)      // @Size max 초과
        );
    }

    private static Stream<String> invalidUpdateDescriptions() {
        return Stream.of(
                "",                 // @Size min 위반
                "a".repeat(501)     // @Size max 초과
        );
    }

    private ResultActions requestUpdateExhibitionDetails(
            Long exhibitionId,
            ExhibitionUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateExhibitionDetailsWithToken(
            Long exhibitionId,
            ExhibitionUpdateRequest request,
            String token
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token)
        );
    }

    private ResultActions requestUpdateExhibitionDetailsAuthenticated(
            Long exhibitionId,
            ExhibitionUpdateRequest request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken)
        );
    }

    private static Stream<String> invalidTitles() {
        return Stream.of(
                null,               // @NotBlank 위반
                "",                 // @NotBlank 위반
                "a".repeat(31)      // 최댓값 초과
        );
    }

    private static Stream<String> invalidDescriptions() {
        return Stream.of(
                null,               // @NotBlank 위반
                "",                 // @NotBlank 위반
                "a".repeat(201)     // 최댓값 초과
        );
    }

    private static Stream<String> invalidCardColors() {
        return Stream.of(
                null,               // @NotBlank 위반
                "",                 // @NotBlank 위반
                "a".repeat(21)      // 최댓값 초과
        );
    }

    private static Stream<MockMultipartFile> invalidImages() {
        MockMultipartFile emptyImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        MockMultipartFile gifImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.gif")
                .contentType("image/gif")
                .content(new byte[]{1})
                .build();

        MockMultipartFile bigImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[5 * 1024 * 1024 + 1])
                .build();

        return Stream.of(
                emptyImage,     // 빈 파일
                gifImage,       // 허용되지 않는 확장자
                bigImage        // 5MB 초과
        );
    }

    private MockMultipartFile createValidImage() {
        return MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{1})
                .build();
    }

    private ResultActions requestOpenExhibition(
            ExhibitionCreateRequest exhibitionRequest,
            List<MockMultipartFile> imageParts
    ) throws Exception {
        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(exhibitionRequest)
                .objectMapper(objectMapper)
                .build();

        MockMultipartHttpServletRequestBuilder builder = multipart(ApiPath.EXHIBITION_ROOT)
                .file(exhibitionPart);

        for (MockMultipartFile imagePart : imageParts) {
            builder = builder.file(imagePart);
        }

        MockHttpServletRequestBuilder httpBuilder = builder.contentType(MediaType.MULTIPART_FORM_DATA);

        return mockMvc.perform(httpBuilder);
    }

    private ResultActions requestOpenExhibitionWithToken(
            ExhibitionCreateRequest exhibitionRequest,
            List<MockMultipartFile> imageParts,
            String token
    ) throws Exception {
        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(exhibitionRequest)
                .objectMapper(objectMapper)
                .build();

        MockMultipartHttpServletRequestBuilder builder = multipart(ApiPath.EXHIBITION_ROOT)
                .file(exhibitionPart);

        for (MockMultipartFile imagePart : imageParts) {
            builder = builder.file(imagePart);
        }

        MockHttpServletRequestBuilder httpBuilder = builder
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + token);

        return mockMvc.perform(httpBuilder);
    }

    private ResultActions requestOpenExhibitionAuthenticated(
            ExhibitionCreateRequest exhibitionRequest,
            List<MockMultipartFile> imageParts
    ) throws Exception {
        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(exhibitionRequest)
                .objectMapper(objectMapper)
                .build();

        MockMultipartHttpServletRequestBuilder builder = multipart(ApiPath.EXHIBITION_ROOT)
                .file(exhibitionPart);

        for (MockMultipartFile imagePart : imageParts) {
            builder = builder.file(imagePart);
        }

        MockHttpServletRequestBuilder httpBuilder = builder
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + accessToken);

        return mockMvc.perform(httpBuilder);
    }

    @Nested
    @DisplayName("전시회 삭제")
    class DeleteExhibitionTest {
        private Exhibition savedExhibition;

        @BeforeEach
        void setUpExhibition() {
            Exhibition exhibition = Exhibition.builder()
                    .writer(savedUser)
                    .title("삭제할 전시회 제목")
                    .description("삭제할 전시회 설명")
                    .cardColor("#FFFFFF")
                    .viewCount(0L)
                    .likeCount(0L)
                    .build();
            savedExhibition = exhibitionCommandPort.save(exhibition);
        }

        @Test
        @DisplayName("요청이 유효하면 전시회를 삭제하고 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // when
            ResultActions resultActions = requestDeleteExhibitionAuthenticated(savedExhibition.getId());
            Optional<Exhibition> deletedExhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isNoContent());
            Assertions.assertThat(deletedExhibition).isNotPresent();
        }

        @Test
        @DisplayName("인증되지 않은 사용자면 전시회를 삭제하지 않고 401을 반환한다")
        public void whenNotAuthenticated() throws Exception {
            // when
            ResultActions resultActions = requestDeleteExhibition(savedExhibition.getId());
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isUnauthorized());
            Assertions.assertThat(exhibition).isPresent();
        }

        @Test
        @DisplayName("존재하지 않는 전시회면 전시회를 삭제하지 않고 204를 반환한다")
        public void whenExhibitionNotFound() throws Exception {
            // when
            ResultActions resultActions = requestDeleteExhibitionAuthenticated(
                    savedExhibition.getId() + 999L
            );

            // then
            resultActions.andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("전시회 소유자가 아니면 전시회를 삭제하지 않고 403을 반환한다")
        public void whenNotOwned() throws Exception {
            // given
            User otherUser = UserFixture.builder()
                    .email("other@example.com")
                    .nickname("다른유저")
                    .build();
            User savedOtherUser = userCommandPort.save(otherUser);
            AuthenticationTokens otherTokens = authenticationTokenManagerPort.issueTokens(
                    savedOtherUser.getId(),
                    savedOtherUser.getRole().name()
            );

            // when
            ResultActions resultActions = requestDeleteExhibitionWithToken(
                    savedExhibition.getId(),
                    otherTokens.getAccessToken()
            );
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isForbidden());
            Assertions.assertThat(exhibition).isPresent();
        }

        @Test
        @DisplayName("아웃박스 이벤트 저장에 실패하면 전시회를 삭제하지 않고 500을 반환한다")
        public void whenOutboxEventSaveFails() throws Exception {
            // given
            Mockito.doThrow(new DataAccessResourceFailureException("DB 에러"))
                    .when(outboxEventPort).save(any());

            // when
            ResultActions resultActions = requestDeleteExhibitionAuthenticated(savedExhibition.getId());
            Optional<Exhibition> exhibition = exhibitionQueryPort.findByIdAndDeletedAtIsNull(
                    savedExhibition.getId()
            );

            // then
            resultActions.andExpect(status().isInternalServerError());
            Assertions.assertThat(exhibition).isPresent();
        }
    }

    private ResultActions requestDeleteExhibition(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_DATA, exhibitionId)
        );
    }

    private ResultActions requestDeleteExhibitionWithToken(
            Long exhibitionId,
            String token
    ) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .header("Authorization", "Bearer " + token)
        );
    }

    private ResultActions requestDeleteExhibitionAuthenticated(Long exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_DATA, exhibitionId)
                        .header("Authorization", "Bearer " + accessToken)
        );
    }
}
