package com.benchpress200.photique.singlework.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.command.port.in.AddSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.application.command.port.in.CancelSingleWorkLikeUseCase;
import com.benchpress200.photique.singlework.domain.exception.SingleWorkAlreadyLikedException;
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
    @DisplayName("단일작품 좋아요 추가 요청 시 요청이 유효하면 201을 반환한다")
    void addSingleWorkLike_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(addSingleWorkLikeUseCase).addSingleWorkLike(any());

        // when
        ResultActions resultActions = requestAddSingleWorkLike("1");

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("단일작품 좋아요 추가 요청 시 작품 ID가 숫자가 아니면 400을 반환한다")
    void addSingleWorkLike_whenSingleWorkIdIsNotNumber() throws Exception {
        // given
        doNothing().when(addSingleWorkLikeUseCase).addSingleWorkLike(any());

        // when
        ResultActions resultActions = requestAddSingleWorkLike("invalid");

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 좋아요 추가 요청 시 이미 좋아요를 눌렀으면 409를 반환한다")
    void addSingleWorkLike_whenAlreadyLiked() throws Exception {
        // given
        doThrow(new SingleWorkAlreadyLikedException(1L, 1L))
                .when(addSingleWorkLikeUseCase).addSingleWorkLike(any());

        // when
        ResultActions resultActions = requestAddSingleWorkLike("1");

        // then
        resultActions.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("단일작품 좋아요 취소 요청 시 요청이 유효하면 204를 반환한다")
    void cancelSingleWorkLike_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(cancelSingleWorkLikeUseCase).cancelSingleWorkLike(any());

        // when
        ResultActions resultActions = requestCancelSingleWorkLike("1");

        // then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("단일작품 좋아요 취소 요청 시 작품 ID가 숫자가 아니면 400을 반환한다")
    void cancelSingleWorkLike_whenSingleWorkIdIsInvalid() throws Exception {
        // given
        doNothing().when(cancelSingleWorkLikeUseCase).cancelSingleWorkLike(any());

        // when
        ResultActions resultActions = requestCancelSingleWorkLike("invalid");

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    private ResultActions requestAddSingleWorkLike(String singleWorkId) throws Exception {
        return mockMvc.perform(
                post(ApiPath.SINGLEWORK_LIKE, singleWorkId)
        );
    }

    private ResultActions requestCancelSingleWorkLike(String singleWorkId) throws Exception {
        return mockMvc.perform(
                delete(ApiPath.SINGLEWORK_LIKE, singleWorkId)
        );
    }
}