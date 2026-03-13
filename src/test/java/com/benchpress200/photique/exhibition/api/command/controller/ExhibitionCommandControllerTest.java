package com.benchpress200.photique.exhibition.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.MultipartKey;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionWorkCreateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCreateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionWorkCreateRequestFixture;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.OpenExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionDetailsUseCase;
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
        controllers = ExhibitionCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 커맨드 컨트롤러 테스트")
public class ExhibitionCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private OpenExhibitionUseCase openExhibitionUseCase;

    @MockitoBean
    private UpdateExhibitionDetailsUseCase updateExhibitionDetailsUseCase;

    @MockitoBean
    private DeleteExhibitionUseCase deleteExhibitionUseCase;

    @Test
    @DisplayName("전시회 생성 요청 시 요청이 유효하면 201을 반환한다")
    public void openExhibition_whenRequestIsValid() throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 제목이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTitles")
    public void openExhibition_whenTitleIsInvalid(String invalidTitle) throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .title(invalidTitle)
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 설명이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidDescriptions")
    public void openExhibition_whenDescriptionIsInvalid(String invalidDescription) throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .description(invalidDescription)
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 카드 색상이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidCardColors")
    public void openExhibition_whenCardColorIsInvalid(String invalidCardColor) throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .cardColor(invalidCardColor)
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 태그 리스트가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidTags")
    public void openExhibition_whenTagsIsInvalid(List<String> invalidTags) throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .tags(invalidTags)
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전시회 생성 요청 시 작품 목록이 null이면 400을 반환한다")
    public void openExhibition_whenWorksIsNull() throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(null)
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전시회 생성 요청 시 작품 목록이 비어있으면 400을 반환한다")
    public void openExhibition_whenWorksIsEmpty() throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(List.of())
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 작품 순서가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidDisplayOrders")
    public void openExhibition_whenWorkDisplayOrderIsInvalid(Integer invalidDisplayOrder) throws Exception {
        // given
        ExhibitionWorkCreateRequest work = ExhibitionWorkCreateRequestFixture.builder()
                .displayOrder(invalidDisplayOrder)
                .build();

        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(List.of(work))
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 작품 제목이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidWorkTitles")
    public void openExhibition_whenWorkTitleIsInvalid(String invalidWorkTitle) throws Exception {
        // given
        ExhibitionWorkCreateRequest work = ExhibitionWorkCreateRequestFixture.builder()
                .title(invalidWorkTitle)
                .build();

        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(List.of(work))
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 작품 설명이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidWorkDescriptions")
    public void openExhibition_whenWorkDescriptionIsInvalid(String invalidWorkDescription) throws Exception {
        // given
        ExhibitionWorkCreateRequest work = ExhibitionWorkCreateRequestFixture.builder()
                .description(invalidWorkDescription)
                .build();

        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(List.of(work))
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전시회 생성 요청 시 작품 순서가 중복되면 400을 반환한다")
    public void openExhibition_whenWorkDisplayOrderIsDuplicated() throws Exception {
        // given
        ExhibitionWorkCreateRequest work1 = ExhibitionWorkCreateRequestFixture.builder()
                .displayOrder(0)
                .build();

        ExhibitionWorkCreateRequest work2 = ExhibitionWorkCreateRequestFixture.builder()
                .displayOrder(0)
                .build();

        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder()
                .works(List.of(work1, work2))
                .build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        MockMultipartFile imagePart = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[]{0})
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, imagePart);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("전시회 생성 요청 시 이미지 파일이 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidImages")
    public void openExhibition_whenImageIsInvalid(MockMultipartFile invalidImage) throws Exception {
        // given
        ExhibitionCreateRequest request = ExhibitionCreateRequestFixture.builder().build();

        MockMultipartFile exhibitionPart = MultipartJsonFixture.builder()
                .key(MultipartKey.EXHIBITION)
                .object(request)
                .objectMapper(objectMapper)
                .build();

        doNothing().when(openExhibitionUseCase).openExhibition(any());

        // when
        ResultActions resultActions = requestOpenExhibition(exhibitionPart, invalidImage);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidTitles() {
        return Stream.of(
                null,
                "",
                "a".repeat(31)
        );
    }

    private static Stream<String> invalidDescriptions() {
        return Stream.of(
                null,
                "",
                "a".repeat(201)
        );
    }

    private static Stream<String> invalidCardColors() {
        return Stream.of(
                null,
                "",
                "a".repeat(21)
        );
    }

    private static Stream<List<String>> invalidTags() {
        return Stream.of(
                List.of("태그1", "태그2", "태그3", "태그4", "태그5", "태그6"), // 태그 6개
                List.of("공백 태그"), // 공백 포함 태그
                List.of("아프리카코끼리위에올라탄앵무새") // 10자 초과 태그
        );
    }

    private static Stream<Integer> invalidDisplayOrders() {
        return Stream.of(
                null,
                -1,
                10
        );
    }

    private static Stream<String> invalidWorkTitles() {
        return Stream.of(
                null,
                "",
                "a".repeat(31)
        );
    }

    private static Stream<String> invalidWorkDescriptions() {
        return Stream.of(
                null,
                "",
                "a".repeat(201)
        );
    }

    private static Stream<MockMultipartFile> invalidImages() {
        MockMultipartFile emptyImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .build();

        MockMultipartFile bigImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.jpg")
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[5 * 1024 * 1024 + 1])
                .build();

        MockMultipartFile noNameImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .contentType(MediaType.IMAGE_JPEG_VALUE)
                .content(new byte[1])
                .build();

        MockMultipartFile gifImage = MultipartFileFixture.builder()
                .key(MultipartKey.IMAGES)
                .fileName("test.gif")
                .contentType(MediaType.IMAGE_GIF_VALUE)
                .content(new byte[1])
                .build();

        return Stream.of(
                emptyImage, // 빈 파일
                bigImage, // 5MB 초과
                noNameImage, // 파일 이름 null
                gifImage // 다른 확장자
        );
    }

    private ResultActions requestOpenExhibition(
            MockMultipartFile exhibitionPart,
            MockMultipartFile imagePart
    ) throws Exception {
        return mockMvc.perform(
                multipart(ApiPath.EXHIBITION_ROOT)
                        .file(exhibitionPart)
                        .file(imagePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );
    }
}
