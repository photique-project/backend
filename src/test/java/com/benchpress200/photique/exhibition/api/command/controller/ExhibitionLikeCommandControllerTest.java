package com.benchpress200.photique.exhibition.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.command.port.in.AddExhibitionLikeUseCase;
import com.benchpress200.photique.exhibition.application.command.port.in.CancelExhibitionLikeUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = ExhibitionLikeCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 좋아요 커맨드 컨트롤러 테스트")
public class ExhibitionLikeCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private AddExhibitionLikeUseCase addExhibitionLikeUseCase;

    @MockitoBean
    private CancelExhibitionLikeUseCase cancelExhibitionLikeUseCase;

    @Test
    @DisplayName("전시회 좋아요 추가 요청 시 요청이 유효하면 201을 반환한다")
    public void addExhibitionLike_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(addExhibitionLikeUseCase).addExhibitionLike(any());

        // when
        ResultActions resultActions = requestAddExhibitionLike("1");

        // then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("전시회 좋아요 추가 요청 시 전시회 ID가 숫자가 아니면 400을 반환한다")
    public void addExhibitionLike_whenExhibitionIdIsNotNumber() throws Exception {
        // given
        doNothing().when(addExhibitionLikeUseCase).addExhibitionLike(any());

        // when
        ResultActions resultActions = requestAddExhibitionLike("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("전시회 좋아요 취소 요청 시 요청이 유효하면 204를 반환한다")
    public void cancelExhibitionLike_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(cancelExhibitionLikeUseCase).cancelExhibitionLike(any());

        // when
        ResultActions resultActions = requestCancelExhibitionLike("1");

        // then
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("전시회 좋아요 취소 요청 시 전시회 ID가 숫자가 아니면 400을 반환한다")
    public void cancelExhibitionLike_whenExhibitionIdIsNotNumber() throws Exception {
        // given
        doNothing().when(cancelExhibitionLikeUseCase).cancelExhibitionLike(any());

        // when
        ResultActions resultActions = requestCancelExhibitionLike("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestAddExhibitionLike(String exhibitionId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.EXHIBITION_LIKE, exhibitionId)
        );
    }

    private ResultActions requestCancelExhibitionLike(String exhibitionId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.EXHIBITION_LIKE, exhibitionId)
        );
    }
}
