package com.benchpress200.photique.user.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.support.base.BaseControllerTest;
import com.benchpress200.photique.user.application.query.port.in.GetMyDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.GetUserDetailsUseCase;
import com.benchpress200.photique.user.application.query.port.in.SearchUserUseCase;
import com.benchpress200.photique.user.application.query.port.in.ValidateNicknameUseCase;
import com.benchpress200.photique.user.application.query.result.MyDetailsResult;
import com.benchpress200.photique.user.application.query.result.NicknameValidateResult;
import com.benchpress200.photique.user.application.query.result.UserDetailsResult;
import com.benchpress200.photique.user.application.query.result.UserSearchResult;
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
        controllers = UserQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("유저 쿼리 컨트롤러 테스트")
public class UserQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private ValidateNicknameUseCase validateNicknameUseCase;

    @MockitoBean
    private GetUserDetailsUseCase getUserDetailsUseCase;

    @MockitoBean
    private GetMyDetailsUseCase getMyDetailsUseCase;

    @MockitoBean
    private SearchUserUseCase searchUserUseCase;

    @Nested
    @DisplayName("닉네임 중복 검사")
    class ValidateNicknameTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            doReturn(NicknameValidateResult.of(false)).when(validateNicknameUseCase).validateNickname(any());

            // when
            ResultActions resultActions = requestValidateNickname("테스트닉");

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @DisplayName("닉네임이 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.query.controller.UserQueryControllerTest#invalidNicknames")
        public void whenNicknameInvalid(String invalidNickname) throws Exception {
            // given
            doReturn(NicknameValidateResult.of(false)).when(validateNicknameUseCase).validateNickname(any());

            // when
            ResultActions resultActions = requestValidateNickname(invalidNickname);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("유저 정보 상세 조회")
    class GetUserDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserDetailsResult result = UserDetailsResult.builder().build();
            doReturn(result).when(getUserDetailsUseCase).getUserDetails(any());

            // when
            ResultActions resultActions = requestGetUserDetails("1");

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("userId가 숫자가 아니면 400을 반환한다")
        public void whenUserIdInvalid() throws Exception {
            // given
            UserDetailsResult result = UserDetailsResult.builder().build();
            doReturn(result).when(getUserDetailsUseCase).getUserDetails(any());

            // when
            ResultActions resultActions = requestGetUserDetails("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("내 정보 상세 조회")
    class GetMyDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            MyDetailsResult result = MyDetailsResult.builder().build();
            doReturn(result).when(getMyDetailsUseCase).getMyDetails();

            // when
            ResultActions resultActions = requestGetMyDetails();

            // then
            resultActions
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("유저 검색")
    class ResisterTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            UserSearchResult result = UserSearchResult.builder().build();
            doReturn(result).when(searchUserUseCase).searchUser(any());

            // when
            ResultActions resultActions = requestSearchUser("테스트닉", null, null);

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.query.controller.UserQueryControllerTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // given
            UserSearchResult result = UserSearchResult.builder().build();
            doReturn(result).when(searchUserUseCase).searchUser(any());

            // when
            ResultActions resultActions = requestSearchUser(invalidKeyword, null, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 음수이면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            UserSearchResult result = UserSearchResult.builder().build();
            doReturn(result).when(searchUserUseCase).searchUser(any());

            // when
            ResultActions resultActions = requestSearchUser("테스트닉", -1, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.user.api.query.controller.UserQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(Integer invalidSize) throws Exception {
            // given
            UserSearchResult result = UserSearchResult.builder().build();
            doReturn(result).when(searchUserUseCase).searchUser(any());

            // when
            ResultActions resultActions = requestSearchUser("테스트닉", null, invalidSize);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidKeywords() {
        return Stream.of(
                null,               // @NotNull 위반
                "key word",         // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
    }

    private static Stream<Integer> invalidSizes() {
        return Stream.of(
                0,  // 최솟값 미만
                51  // 최댓값 초과
        );
    }

    private static Stream<String> invalidNicknames() {
        return Stream.of(
                null,               // @NotNull 위반
                "nick name",        // 공백 포함
                "a".repeat(12)      // 12자 (최댓값 초과)
        );
    }

    private ResultActions requestSearchUser(
            String keyword,
            Integer page,
            Integer size
    ) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.USER_ROOT);

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

    private ResultActions requestGetMyDetails() throws Exception {
        return mockMvc.perform(get(ApiPath.USER_MY_DATA));
    }

    private ResultActions requestGetUserDetails(String userId) throws Exception {
        return mockMvc.perform(get(ApiPath.USER_DATA, userId));
    }

    private ResultActions requestValidateNickname(String nickname) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.USER_NICKNAME_EXISTS);

        if (nickname != null) {
            builder = builder.param("nickname", nickname);
        }

        return mockMvc.perform(builder);
    }
}
