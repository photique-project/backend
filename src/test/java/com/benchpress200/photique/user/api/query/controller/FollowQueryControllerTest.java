package com.benchpress200.photique.user.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.user.application.query.port.in.SearchFolloweeUseCase;
import com.benchpress200.photique.user.application.query.port.in.SearchFollowerUseCase;
import com.benchpress200.photique.user.application.query.result.FolloweeSearchResult;
import com.benchpress200.photique.user.application.query.result.FollowerSearchResult;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(
        controllers = FollowQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("팔로우 쿼리 컨트롤러 테스트")
public class FollowQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private SearchFollowerUseCase searchFollowerUseCase;

    @MockitoBean
    private SearchFolloweeUseCase searchFolloweeUseCase;

    @Nested
    @DisplayName("팔로워 검색")
    class SearchFollowerTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            FollowerSearchResult result = FollowerSearchResult.builder().build();
            doReturn(result).when(searchFollowerUseCase).searchFollower(any());

            // when
            ResultActions resultActions = requestSearchFollower("1", null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            FollowerSearchResult result = FollowerSearchResult.builder().build();
            doReturn(result).when(searchFollowerUseCase).searchFollower(any());

            // when
            ResultActions resultActions = requestSearchFollower("invalid", null, null, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            FollowerSearchResult result = FollowerSearchResult.builder().build();
            doReturn(result).when(searchFollowerUseCase).searchFollower(any());

            // when
            ResultActions resultActions = requestSearchFollower("1", null, -1, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.query.controller.FollowQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(Integer invalidSize) throws Exception {
            // given
            FollowerSearchResult result = FollowerSearchResult.builder().build();
            doReturn(result).when(searchFollowerUseCase).searchFollower(any());

            // when
            ResultActions resultActions = requestSearchFollower("1", null, null, invalidSize);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("팔로이 검색")
    class SearchFolloweeTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            FolloweeSearchResult result = FolloweeSearchResult.builder().build();
            doReturn(result).when(searchFolloweeUseCase).searchFollowee(any());

            // when
            ResultActions resultActions = requestSearchFollowee("1", null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            FolloweeSearchResult result = FolloweeSearchResult.builder().build();
            doReturn(result).when(searchFolloweeUseCase).searchFollowee(any());

            // when
            ResultActions resultActions = requestSearchFollowee("invalid", null, null, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            FolloweeSearchResult result = FolloweeSearchResult.builder().build();
            doReturn(result).when(searchFolloweeUseCase).searchFollowee(any());

            // when
            ResultActions resultActions = requestSearchFollowee("1", null, -1, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.query.controller.FollowQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(Integer invalidSize) throws Exception {
            // given
            FolloweeSearchResult result = FolloweeSearchResult.builder().build();
            doReturn(result).when(searchFolloweeUseCase).searchFollowee(any());

            // when
            ResultActions resultActions = requestSearchFollowee("1", null, null, invalidSize);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<Integer> invalidSizes() {
        return Stream.of(
                0,  // 최솟값 미만
                51  // 최댓값 초과
        );
    }

    private ResultActions requestSearchFollowee(
            String userId,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.FOLLOWEE, userId);

        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }

        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }

        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }

        return mockMvc.perform(builder);
    }

    private ResultActions requestSearchFollower(
            String userId,
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.FOLLOWER, userId);

        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }

        if (page != null) {
            builder = builder.param("page", String.valueOf(page));
        }

        if (size != null) {
            builder = builder.param("size", String.valueOf(size));
        }

        return mockMvc.perform(builder);
    }
}
