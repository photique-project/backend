package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.UserQueryService;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.SearchUsersResult;
import com.benchpress200.photique.user.application.result.SearchedUser;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@DisplayName("UserQueryController 테스트")
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Security Filter 비활성화
public class UserQueryControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserQueryService userQueryService;

    @Test
    @DisplayName("validateNickname 성공 테스트")
    void validateNickname_성공_테스트() throws Exception {
        // GIVEN
        String validNickname = "nickname";
        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME + "?nickname=" + validNickname)
                .accept(MediaType.APPLICATION_JSON);

        boolean result = false;
        ValidateNicknameResult validateNicknameResult = ValidateNicknameResult.of(result);
        Mockito.doReturn(validateNicknameResult).when(userQueryService).validateNickname(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.isDuplicated").value(false));
    }

    @ParameterizedTest
    @MethodSource("getInvalidNicknames")
    @DisplayName("validateNickname 실패 테스트 - 유효하지 않은 닉네임")
    void validateNickname_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME + "?nickname=" + invalidNickname)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("getUserDetails 성공 테스트")
    void getUserDetails_성공_테스트() throws Exception {
        // GIVEN
        long userId = 1L;
        String nickname = "nickname";
        long singleWorkCount = 0L;
        long exhibitionCount = 0L;
        long followerCount = 0L;
        long followingCount = 0L;
        boolean isFollowing = false;

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + "/" + userId)
                .accept(MediaType.APPLICATION_JSON);

        UserDetailsResult userDetailsResult = UserDetailsResult.builder()
                .userId(userId)
                .nickname(nickname)
                .singleWorkCount(singleWorkCount)
                .exhibitionCount(exhibitionCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .isFollowing(isFollowing)
                .build();

        Mockito.doReturn(userDetailsResult).when(userQueryService).getUserDetails(userId);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andExpect(jsonPath("$.data.profileImage").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.data.singleWorkCount").value(singleWorkCount))
                .andExpect(jsonPath("$.data.exhibitionCount").value(exhibitionCount))
                .andExpect(jsonPath("$.data.followerCount").value(followerCount))
                .andExpect(jsonPath("$.data.followingCount").value(followingCount))
                .andExpect(jsonPath("$.data.isFollowing").value(isFollowing));
    }

    @Test
    @DisplayName("getUserDetails 실패 테스트 - 유효하지 않은 경로 변수")
    void getUserDetails_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + "/a")
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("getMyDetails 성공 테스트")
    void getMyDetails_성공_테스트() throws Exception {
        // GIVEN
        Long userId = 1L;
        String email = "example@example.com";
        String nickname = "nickname";
        String introduction = "introduction";
        Long singleWorkCount = 0L;
        Long exhibitionCount = 0L;
        Long followerCount = 0L;
        Long followingCount = 0L;

        MyDetailsResult myDetailsResult = MyDetailsResult.builder()
                .userId(userId)
                .email(email)
                .nickname(nickname)
                .introduction(introduction)
                .singleWorkCount(singleWorkCount)
                .exhibitionCount(exhibitionCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .build();

        Mockito.doReturn(myDetailsResult).when(userQueryService).getMyDetails();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.MY_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.nickname").value(nickname))
                .andExpect(jsonPath("$.data.introduction").value(introduction))
                .andExpect(jsonPath("$.data.singleWorkCount").value(singleWorkCount))
                .andExpect(jsonPath("$.data.exhibitionCount").value(exhibitionCount))
                .andExpect(jsonPath("$.data.followerCount").value(followerCount))
                .andExpect(jsonPath("$.data.followingCount").value(followingCount));
    }

    @Test
    @DisplayName("searchUsers 성공 테스트")
    void searchUsers_성공_테스트() throws Exception {
        // GIVEN
        String keyword = "nickname";
        String page = "0";
        String size = "30";

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param("keyword", keyword)
                .param("page", page)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON);

        SearchedUser searchedUser = SearchedUser.builder().build();

        SearchUsersResult searchUsersResult = SearchUsersResult.builder()
                .page(0)
                .size(30)
                .totalElements(1)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .hasNext(false)
                .hasPrevious(false)
                .users(List.of(searchedUser))
                .build();
        Mockito.doReturn(searchUsersResult).when(userQueryService).searchUsers(Mockito.any());

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @ParameterizedTest
    @MethodSource("getInvalidNicknames")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은 닉네임")
    void searchUsers_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        String page = "0";
        String size = "30";

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param("keyword", invalidNickname)
                .param("page", page)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @MethodSource("getInvalidPages")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은 페이지")
    void searchUsers_실패_테스트_유효하지_않은_페이지(final String page) throws Exception {
        // GIVEN
        String keyword = "nickname";
        String size = "30";

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param("keyword", keyword)
                .param("page", page)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @ParameterizedTest
    @MethodSource("getInvalidPageSizes")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은페이지 사이즈")
    void searchUsers_실패_테스트_유효하지_않은_페이지_사이즈(final String size) throws Exception {
        // GIVEN
        String keyword = "nickname";
        String page = "0";

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param("keyword", keyword)
                .param("page", page)
                .param("size", size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }


    // 유효하지 않은 닉네임
    static Stream<String> getInvalidNicknames() {
        return Stream.of(
                "",     // 빈 문자열
                " ",             // 공백
                "abcdefghijkl",  // 12자 초과
                "nick name"      // 공백 포함
        );
    }

    // 유효하지 않은 페이지
    static Stream<String> getInvalidPages() {
        return Stream.of(
                "-1",
                "a"
        );
    }

    // 유효하지 않은 페이지 사이즈
    static Stream<String> getInvalidPageSizes() {
        return Stream.of(
                "0",
                "51",
                "a"
        );
    }
}
