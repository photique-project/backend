package com.benchpress200.photique.singlework.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = SingleWorkLikeCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("단일작품 좋아요 커맨드 컨트롤러 테스트")
public class SingleWorkLikeCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private AddSingleWorkLikeUseCase addSingleWorkLikeUseCase;

    @MockitoBean
    private CancelSingleWorkLikeUseCase cancelSingleWorkLikeUseCase;


    @Test
    @DisplayName("단일작품 좋아요 취소 요청 시 요청이 유효하면 204를 반환한다")
    public void cancelSingleWorkLike_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(cancelSingleWorkLikeUseCase).cancelSingleWorkLike(any());

        // when
        ResultActions resultActions = requestCancelSingleWorkLike("1");

        // then
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("단일작품 좋아요 취소 요청 시 작품 ID가 숫자가 아니면 400을 반환한다")
    public void cancelSingleWorkLike_whenSingleWorkIdIsInvalid() throws Exception {
        // given
        doNothing().when(cancelSingleWorkLikeUseCase).cancelSingleWorkLike(any());

        // when
        ResultActions resultActions = requestCancelSingleWorkLike("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestCancelSingleWorkLike(String singleWorkId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_LIKE, singleWorkId)
        );
    }
}
