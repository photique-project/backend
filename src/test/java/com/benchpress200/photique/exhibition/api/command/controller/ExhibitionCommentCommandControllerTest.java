package com.benchpress200.photique.exhibition.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentCreateRequest;
import com.benchpress200.photique.exhibition.api.command.request.ExhibitionCommentUpdateRequest;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCommentCreateRequestFixture;
import com.benchpress200.photique.exhibition.api.command.support.fixture.ExhibitionCommentUpdateRequestFixture;
import com.benchpress200.photique.exhibition.application.command.port.in.CreateExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.DeleteExhibitionCommentUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.UpdateExhibitionCommentUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = ExhibitionCommentCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 감상평 커맨드 컨트롤러 테스트")
public class ExhibitionCommentCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private CreateExhibitionCommentUseCase createExhibitionCommentUseCase;

    @MockitoBean
    private UpdateExhibitionCommentUseCase updateExhibitionCommentUseCase;

    @MockitoBean
    private DeleteExhibitionCommentUseCase deleteExhibitionCommentUseCase;

    @Nested
    @DisplayName("전시회 감상평 생성")
    class CreateExhibitionCommentTest {
        @Test
        @DisplayName("요청이 유효하면 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder().build();
            doNothing().when(createExhibitionCommentUseCase).createExhibitionComment(any());

            // when
            ResultActions resultActions = requestCreateExhibitionComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest
        @DisplayName("내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.command.controller.ExhibitionCommentCommandControllerTest#invalidContents")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            ExhibitionCommentCreateRequest request = ExhibitionCommentCreateRequestFixture.builder()
                    .content(invalidContent)
                    .build();
            doNothing().when(createExhibitionCommentUseCase).createExhibitionComment(any());

            // when
            ResultActions resultActions = requestCreateExhibitionComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("전시회 감상평 수정")
    class UpdateExhibitionCommentTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder().build();
            doNothing().when(updateExhibitionCommentUseCase).updateExhibitionComment(any());

            // when
            ResultActions resultActions = requestUpdateExhibitionComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @DisplayName("내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.command.controller.ExhibitionCommentCommandControllerTest#invalidContentsForUpdate")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            ExhibitionCommentUpdateRequest request = ExhibitionCommentUpdateRequestFixture.builder()
                    .content(invalidContent)
                    .build();
            doNothing().when(updateExhibitionCommentUseCase).updateExhibitionComment(any());

            // when
            ResultActions resultActions = requestUpdateExhibitionComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("전시회 감상평 삭제")
    class DeleteExhibitionCommentTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doNothing().when(deleteExhibitionCommentUseCase).deleteExhibitionComment(any());

            // when
            ResultActions resultActions = requestDeleteExhibitionComment("1");

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("감상평 ID가 유효하지 않다면 400을 반환한다")
        public void whenCommentIdInvalid() throws Exception {
            // given
            doNothing().when(deleteExhibitionCommentUseCase).deleteExhibitionComment(any());

            // when
            ResultActions resultActions = requestDeleteExhibitionComment("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidContents() {
        return Stream.of(
                null,
                "",
                " ",
                "a".repeat(301)
        );
    }

    private ResultActions requestCreateExhibitionComment(
            Long exhibitionId,
            Object request
    ) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_COMMENT, exhibitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private static Stream<String> invalidContentsForUpdate() {
        return Stream.of(
                null,
                "",
                " ",
                "a".repeat(301)
        );
    }

    private ResultActions requestUpdateExhibitionComment(
            Long commentId,
            Object request
    ) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestDeleteExhibitionComment(String commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_COMMENT_DATA, commentId)
        );
    }
}
