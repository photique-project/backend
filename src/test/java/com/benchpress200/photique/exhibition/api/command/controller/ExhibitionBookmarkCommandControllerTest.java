package com.benchpress200.photique.exhibition.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionBookmarkUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionBookmarkUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("전시회 북마크 추가 요청 시 요청이 유효하면 201을 반환한다")
    public void addExhibitionBookmark_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(addExhibitionBookmarkUseCase).addExhibitionBookmark(any());

        // when
        ResultActions resultActions = requestAddExhibitionBookmark("1");

        // then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("전시회 북마크 추가 요청 시 전시회 ID가 숫자가 아니면 400을 반환한다")
    public void addExhibitionBookmark_whenExhibitionIdIsNotNumber() throws Exception {
        // given
        doNothing().when(addExhibitionBookmarkUseCase).addExhibitionBookmark(any());

        // when
        ResultActions resultActions = requestAddExhibitionBookmark("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestAddExhibitionBookmark(String exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_BOOKMARK, exhibitionId)
        );
    }
}
