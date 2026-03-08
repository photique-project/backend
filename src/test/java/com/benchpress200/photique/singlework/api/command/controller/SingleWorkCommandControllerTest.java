package com.benchpress200.photique.singlework.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkUpdateRequest;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCreateRequestFixture;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkUpdateRequestFixture;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.PostSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkDetailsUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.support.fixture.MultipartFileFixture;
import com.benchpress200.photique.support.fixture.MultipartJsonFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(
        controllers = SingleWorkCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class, // Security 기본 옵션 제외
                SecurityFilterAutoConfiguration.class // Security 필터 제외
        }
)
@DisplayName("단일작품 커맨드 컨트롤러 테스트")
public class SingleWorkCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private PostSingleWorkUseCase postSingleWorkUseCase;

    @MockitoBean
    private UpdateSingleWorkDetailsUseCase updateSingleWorkDetailsUseCase;

    @MockitoBean
    private DeleteSingleWorkUseCase deleteSingleWorkUseCase;


    @Test
    @DisplayName("단일작품 생성 요청 시 요청이 유효하면 201을 반환한다")
    public void postSingleWork_whenRequestIsValid() throws Exception {
        // given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build(); // 기본값으로 객체 생성

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @DisplayName("단일작품 생성 요청 시 제목이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTitle") // null, 빈 문자열, 31자 제목
    public void postSingleWork_whenTitleIsInvalid(String invalidTitle) throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .title(invalidTitle)
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        //when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 생성 요청 시 설명이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidDescriptions") // null, 빈 문자열, 501자 설명
    public void postSingleWork_whenDescriptionIsInvalid(String invalidDescription) throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .description(invalidDescription)
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        //when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 생성 요청 시 카메라 이름이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidCamera") // null, 빈 문자열, 31자 카메라 이름
    public void postSingleWork_whenCameraIsInvalid(String invalidCamera) throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .camera(invalidCamera)
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        //when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 렌즈 이름이 유효하지 않으면 400을 반환한다")
    public void postSingleWork_whenLensIsInvalid() throws Exception {
        // given
        int invalidLength = 31; // 30자 초과
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .lens("a".repeat(invalidLength))
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 조리개 값이 유효하지 않다면(enum에 속하지 않는 값) 400을 반환한다")
    public void postSingleWork_whenApertureIsInvalid() throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .aperture("f/123")
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 셔터스피드 값이 유효하지 않다면(enum에 속하지 않는 값) 400을 반환한다")
    public void postSingleWork_whenShutterSpeedIsInvalid() throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .shutterSpeed("-50")
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 ISO 값이 유효하지 않다면(enum에 속하지 않는 값) 400을 반환한다")
    public void postSingleWork_whenISOIsInvalid() throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .iso("-1000")
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 카테고리가 유효하지 않다면(enum에 속하지 않는 값) 400을 반환한다")
    public void postSingleWork_whenCategoryIsInvalid() throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .category("두릅비빔")
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 장소 이름이 유효하지 않다면 400을 반환한다")
    public void postSingleWork_whenLocationIsInvalid() throws Exception {
        // given
        int invalidLength = 31; // 30자 초과
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .location("a".repeat(invalidLength))
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 생성 요청 시 촬영 날짜 포멧(yyyy-MM-dd)이 유효하지 않다면 400을 반환한다")
    public void postSingleWork_whenDateIsInvalid() throws Exception {
        // given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .date(null)
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 생성 요청 시 태그 리스트가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTag") // 태그 개수 5개 초과, 공백 포함 태그, 태그 길이 10자 초과
    public void postSingleWork_whenTagIsInvalid(List<String> invalidTag) throws Exception {
        //given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder()
                .tags(invalidTag)
                .build();

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        //when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, imagePart);

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 생성 요청 시 이미지 파일이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidImage") // 빈 파일, 5MB초과, 파일 이름 null, 다른 확장자
    public void postSingleWork_whenImageIsInvalid(MockMultipartFile invalidImage) throws Exception {
        // given
        SingleWorkCreateRequest request = SingleWorkCreateRequestFixture.builder().build(); // 기본값으로 객체 생성

        MockMultipartFile singleWorkPart = MultipartJsonFixture.builder()
                .key(MultipartKey.SINGLEWORK)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        doNothing().when(postSingleWorkUseCase).postSingleWork(any());

        // when
        ResultActions resultActions = requestPostSingleWork(singleWorkPart, invalidImage);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 요청이 유효하면 204를 반환한다")
    public void updateSingleWorkDetails_whenRequestIsValid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder().build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @DisplayName("단일작품 수정 요청 시 제목이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTitleForUpdate")
    public void updateSingleWorkDetails_whenTitleIsInvalid(String invalidTitle) throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateTitle(true)
                .title(invalidTitle)
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 수정 요청 시 설명이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidDescriptionForUpdate")
    public void updateSingleWorkDetails_whenDescriptionIsInvalid(String invalidDescription) throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateDescription(true)
                .description(invalidDescription)
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 수정 요청 시 카메라 이름이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidCameraForUpdate")
    public void updateSingleWorkDetails_whenCameraIsInvalid(String invalidCamera) throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateCamera(true)
                .camera(invalidCamera)
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 렌즈 이름이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenLensIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateLens(true)
                .lens("a".repeat(31))
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 조리개 값이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenApertureIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateAperture(true)
                .aperture("f/123")
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 셔터스피드 값이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenShutterSpeedIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateShutterSpeed(true)
                .shutterSpeed("-50")
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 ISO 값이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenISOIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateIso(true)
                .iso("-1000")
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 카테고리 값이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenCategoryIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateCategory(true)
                .category("두릅비빔")
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 수정 요청 시 장소 이름이 유효하지 않으면 400을 반환한다")
    public void updateSingleWorkDetails_whenLocationIsInvalid() throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateLocation(true)
                .location("a".repeat(31))
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 수정 요청 시 태그 리스트가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTagForUpdate")
    public void updateSingleWorkDetails_whenTagIsInvalid(List<String> invalidTags) throws Exception {
        // given
        SingleWorkUpdateRequest request = SingleWorkUpdateRequestFixture.builder()
                .updateTags(true)
                .tags(invalidTags)
                .build();
        doNothing().when(updateSingleWorkDetailsUseCase).updateSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestUpdateSingleWork(1L, request);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidTitleForUpdate() {
        return Stream.of(
                "",
                "a".repeat(31)
        );
    }

    private static Stream<String> invalidDescriptionForUpdate() {
        return Stream.of(
                "",
                "a".repeat(501)
        );
    }

    private static Stream<String> invalidCameraForUpdate() {
        return Stream.of(
                "",
                "a".repeat(31)
        );
    }

    private static Stream<List<String>> invalidTagForUpdate() {
        return Stream.of(
                List.of("첫번째태그", "두번째태그", "세번째태그", "네번째태그", "다섯번째태그", "여섯번째태그"), // 태그 6개
                List.of("날아가는 새"), // 공백 포함 태그
                List.of("아프리카코끼리위에올라탄앵무새") // 11자 이상 태그
        );
    }

    private static Stream<MockMultipartFile> invalidImage() {
        MockMultipartFile emptyImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        MockMultipartFile bigImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[5 * 1024 * 1024 + 1])
                .build();

        MockMultipartFile noNameImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[1])
                .build();

        MockMultipartFile gifImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGE)
                .fileName("test.gif")
                .contentType(MediaType.IMAGE_GIF_VALUE)
                .content(new byte[1])
                .build();

        return Stream.of(
                emptyImage, // 빈 파일
                bigImage, // 5MB초과
                noNameImage, // 파일 이름 null
                gifImage // 다른 확장자
        );
    }

    private static Stream<List<String>> invalidTag() {
        return Stream.of(
                List.of("첫번째태그, 두번째태그, 세번째태그, 네번째태그, 다섯번째태그, 여섯번째태그"), // 태그 6개
                List.of("날아가는 새"), // 공백 포함 태그
                List.of("고양이", "강아지", "아프리카코끼리위에올라탄앵무새") // 11자 이상 태그
        );
    }

    private static Stream<String> invalidCamera() {
        int length = 31;

        return Stream.of(
                null,
                "",
                "a".repeat(length)  // 31자
        );
    }

    private static Stream<String> invalidDescriptions() {
        int length = 501;

        return Stream.of(
                null,
                "",
                "a".repeat(length)  // 501자
        );
    }

    private static Stream<String> invalidTitle() {
        int length = 31;

        return Stream.of(
                null,
                "",
                "a".repeat(length)  // 31자
        );
    }

    private ResultActions requestUpdateSingleWork(Long singleWorkId, Object request) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_DATA, singleWorkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestPostSingleWork(
            MockMultipartFile singleWorkPart,
            MockMultipartFile imagePart

    ) throws Exception {
        return mockMvc.perform(
                multipart(ApiPath.SINGLEWORK_ROOT)
                        .file(singleWorkPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
    }
}
