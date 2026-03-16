package com.benchpress200.photique.user.api.command.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.user.application.command.port.in.FollowUseCase;
import com.benchpress200.photique.user.application.command.port.in.UnfollowUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
        controllers = FollowCommandController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("팔로우 커맨드 컨트롤러 테스트")
public class FollowCommandControllerTest extends BaseControllerTest {

    @MockitoBean
    private FollowUseCase followUseCase;

    @MockitoBean
    private UnfollowUseCase unfollowUseCase;

    @Test
    @DisplayName("팔로우 요청 시 요청이 유효하면 201을 반환한다")
    public void follow_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(followUseCase).follow(any());

        // when
        ResultActions resultActions = requestFollow("1");

        // then
        resultActions
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("팔로우 요청 시 followeeId가 숫자가 아니면 400을 반환한다")
    public void follow_whenFolloweeIdIsNotNumber() throws Exception {
        // given
        doNothing().when(followUseCase).follow(any());

        // when
        ResultActions resultActions = requestFollow("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("팔로우 취소 요청 시 요청이 유효하면 204를 반환한다")
    public void unfollow_whenRequestIsValid() throws Exception {
        // given
        doNothing().when(unfollowUseCase).unfollow(any());

        // when
        ResultActions resultActions = requestUnfollow("1");

        // then
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("팔로우 취소 요청 시 followeeId가 숫자가 아니면 400을 반환한다")
    public void unfollow_whenFolloweeIdIsNotNumber() throws Exception {
        // given
        doNothing().when(unfollowUseCase).unfollow(any());

        // when
        ResultActions resultActions = requestUnfollow("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestFollow(String followeeId) throws Exception {
        return mockMvc.perform(post(ApiPath.FOLLOW_ROOT, followeeId));
    }

    private ResultActions requestUnfollow(String followeeId) throws Exception {
        return mockMvc.perform(delete(ApiPath.FOLLOW_ROOT, followeeId));
    }
}
