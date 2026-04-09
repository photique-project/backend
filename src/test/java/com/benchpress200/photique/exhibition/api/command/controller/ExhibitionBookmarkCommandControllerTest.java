package com.benchpress200.photique.exhibition.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionBookmarkUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionBookmarkUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = ExhibitionBookmarkCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 북마크 커맨드 컨트롤러 테스트")
public class ExhibitionBookmarkCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private AddExhibitionBookmarkUseCase addExhibitionBookmarkUseCase;

    @MockitoBean
    private CancelExhibitionBookmarkUseCase cancelExhibitionBookmarkUseCase;

    @Nested
    @DisplayName("전시회 북마크 추가")
    class AddExhibitionBookmarkTest {
        @Test
        @DisplayName("요청이 유효하면 201을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doNothing().when(addExhibitionBookmarkUseCase).addExhibitionBookmark(any());

            // when
            ResultActions resultActions = requestAddExhibitionBookmark("1");

            // then
            resultActions
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("전시회 ID가 유효하지 않으면 400을 반환한다")
        public void whenExhibitionIdInvalid() throws Exception {
            // given
            doNothing().when(addExhibitionBookmarkUseCase).addExhibitionBookmark(any());

            // when
            ResultActions resultActions = requestAddExhibitionBookmark("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("전시회 북마크 취소")
    class CancelExhibitionBookmark {
        @Test
        @DisplayName("요청이 유효하면 204를 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doNothing().when(cancelExhibitionBookmarkUseCase).cancelExhibitionBookmark(any());

            // when
            ResultActions resultActions = requestCancelExhibitionBookmark("1");

            // then
            resultActions
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("전시회 ID가 유효하지 않다면 400을 반환한다")
        public void whenExhibitionIdInvalid() throws Exception {
            // given
            doNothing().when(cancelExhibitionBookmarkUseCase).cancelExhibitionBookmark(any());

            // when
            ResultActions resultActions = requestCancelExhibitionBookmark("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private ResultActions requestAddExhibitionBookmark(String exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
        );
    }

    private ResultActions requestCancelExhibitionBookmark(String exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
        );
    }
}
