package com.benchpress200.photique.singlework.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentCreateRequest;
import com.benchpress200.photique.singlework.api.command.request.SingleWorkCommentUpdateRequest;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCommentCreateRequestFixture;
import com.benchpress200.photique.singlework.api.command.support.fixture.SingleWorkCommentUpdateRequestFixture;
import com.benchpress200.photique.singlework.application.command.port.in.CreateSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.DeleteSingleWorkCommentUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.UpdateSingleWorkCommentUseCase;
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
        controllers = SingleWorkCommentCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("단일작품 댓글 커맨드 컨트롤러 테스트")
public class SingleWorkCommentCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private CreateSingleWorkCommentUseCase createSingleWorkCommentUseCase;

    @MockitoBean
    private UpdateSingleWorkCommentUseCase updateSingleWorkCommentUseCase;

    @MockitoBean
    private DeleteSingleWorkCommentUseCase deleteSingleWorkCommentUseCase;

    @Nested
    @DisplayName("단일작품 댓글 생성")
    class CreateSingleWorkCommentTest {
        @Test
        @DisplayName("요청이 유효하면 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder().build();
            doNothing().when(createSingleWorkCommentUseCase).createSingleWorkComment(any());

            // when
            ResultActions resultActions = requestCreateSingleWorkComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isCreated());
        }

        @ParameterizedTest
        @DisplayName("내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.singlework.api.command.controller.SingleWorkCommentCommandControllerTest#invalidContent")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            SingleWorkCommentCreateRequest request = SingleWorkCommentCreateRequestFixture.builder()
                    .content(invalidContent)
                    .build();
            doNothing().when(createSingleWorkCommentUseCase).createSingleWorkComment(any());

            // when
            ResultActions resultActions = requestCreateSingleWorkComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("단일작품 댓글 수정")
    class UpdateSingleWorkCommentTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder().build();
            doNothing().when(updateSingleWorkCommentUseCase).updateSingleWorkComment(any());

            // when
            ResultActions resultActions = requestUpdateSingleWorkComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @DisplayName("내용이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.singlework.api.command.controller.SingleWorkCommentCommandControllerTest#invalidContentForUpdate")
        public void whenContentInvalid(String invalidContent) throws Exception {
            // given
            SingleWorkCommentUpdateRequest request = SingleWorkCommentUpdateRequestFixture.builder()
                    .content(invalidContent)
                    .build();
            doNothing().when(updateSingleWorkCommentUseCase).updateSingleWorkComment(any());

            // when
            ResultActions resultActions = requestUpdateSingleWorkComment(1L, request);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("단일작품 댓글 삭제")
    class DeleteSingleWorkCommentTest {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doNothing().when(deleteSingleWorkCommentUseCase).deleteSingleWorkComment(any());

            // when
            ResultActions resultActions = requestDeleteSingleWorkComment("1");

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("댓글 ID가 숫자가 아니면 400을 반환한다")
        public void whenCommentIdInvalid() throws Exception {
            // given
            doNothing().when(deleteSingleWorkCommentUseCase).deleteSingleWorkComment(any());

            // when
            ResultActions resultActions = requestDeleteSingleWorkComment("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidContent() {
        return Stream.of(
                null,
                "",
                "a".repeat(301)
        );
    }

    private static Stream<String> invalidContentForUpdate() {
        return Stream.of(
                null,
                "",
                "a".repeat(301)
        );
    }

    private ResultActions requestCreateSingleWorkComment(Long singleWorkId, Object request) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_COMMENT, singleWorkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestUpdateSingleWorkComment(Long commentId, Object request) throws Exception {
        return mockMvc.perform(
                patch(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions requestDeleteSingleWorkComment(String commentId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_COMMENT_DATA, commentId)
        );
    }
}
