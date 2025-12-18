package com.benchpress200.photique.user.presentation;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.TestContainerConfiguration;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.user.application.UserQueryService;
import com.benchpress200.photique.user.application.result.MyDetailsResult;
import com.benchpress200.photique.user.application.result.UserDetailsResult;
import com.benchpress200.photique.user.application.result.UserSearchResult;
import com.benchpress200.photique.user.application.result.ValidateNicknameResult;
import com.benchpress200.photique.util.DummyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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
@Import(TestContainerConfiguration.class)
public class UserQueryControllerTest {
    private final static String QUERY_PARAM_NICKNAME = "nickname";
    private final static String QUERY_PARAM_KEYWORD = "keyword";
    private final static String QUERY_PARAM_PAGE = "page";
    private final static String QUERY_PARAM_SIZE = "size";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserQueryService userQueryService;

    @Test
    @DisplayName("validateNickname 성공 테스트")
    void validateNickname_성공_테스트() throws Exception {
        // GIVEN
        String validNickname = DummyGenerator.generateNickname();
        boolean result = false;
        ValidateNicknameResult validateNicknameResult = DummyGenerator.generateValidateNicknameResult(result);
        Mockito.doReturn(validateNicknameResult).when(userQueryService).validateNickname(Mockito.any());

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME)
                .param(QUERY_PARAM_NICKNAME, validNickname)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.isDuplicated").value(result));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidNicknames")
    @DisplayName("validateNickname 실패 테스트 - 유효하지 않은 닉네임")
    void validateNickname_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.VALIDATE_NICKNAME)
                .param(QUERY_PARAM_NICKNAME, invalidNickname)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("getUserDetails 성공 테스트")
    void getUserDetails_성공_테스트() throws Exception {
        // GIVEN
        long userId = 1L;
        UserDetailsResult userDetailsResult = DummyGenerator.generateUserDetailsResult(userId);
        Mockito.doReturn(userDetailsResult).when(userQueryService).getUserDetails(userId);

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, userId)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.userId").value(userId));
    }

    @Test
    @DisplayName("getUserDetails 실패 테스트 - 유효하지 않은 경로 변수")
    void getUserDetails_실패_테스트_유효하지_않은_경로_변수() throws Exception {
        // GIVEN
        String invalidPathVariable = DummyGenerator.generateInvalidPathVariable();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.USER_DATA, invalidPathVariable)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("getMyDetails 성공 테스트")
    void getMyDetails_성공_테스트() throws Exception {
        // GIVEN
        long userId = 1L;
        MyDetailsResult myDetailsResult = DummyGenerator.generateMyDetailsResult(userId);
        Mockito.doReturn(myDetailsResult).when(userQueryService).getMyDetails();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN + URL.MY_DATA)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.userId").value(userId));
    }

    @Test
    @DisplayName("searchUsers 성공 테스트")
    void searchUsers_성공_테스트() throws Exception {
        // GIVEN
        String keyword = DummyGenerator.generateNickname();
        String page = DummyGenerator.generatePage();
        String size = DummyGenerator.generateSize();
        UserSearchResult userSearchResult = DummyGenerator.generateUserSearchResult();
        Mockito.doReturn(userSearchResult).when(userQueryService).searchUsers(Mockito.any());

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param(QUERY_PARAM_KEYWORD, keyword)
                .param(QUERY_PARAM_PAGE, page)
                .param(QUERY_PARAM_SIZE, size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidNicknames")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은 닉네임")
    void searchUsers_실패_테스트_유효하지_않은_닉네임(final String invalidNickname) throws Exception {
        // GIVEN
        String page = DummyGenerator.generatePage();
        String size = DummyGenerator.generateSize();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param(QUERY_PARAM_KEYWORD, invalidNickname)
                .param(QUERY_PARAM_PAGE, page)
                .param(QUERY_PARAM_SIZE, size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidPages")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은 페이지")
    void searchUsers_실패_테스트_유효하지_않은_페이지(final String invalidPage) throws Exception {
        // GIVEN
        String keyword = DummyGenerator.generateNickname();
        String size = DummyGenerator.generateSize();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param(QUERY_PARAM_KEYWORD, keyword)
                .param(QUERY_PARAM_PAGE, invalidPage)
                .param(QUERY_PARAM_SIZE, size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("com.benchpress200.photique.util.DummyGenerator#generateInvalidSizes")
    @DisplayName("searchUsers 실패 테스트 - 유효하지 않은 페이지 사이즈")
    void searchUsers_실패_테스트_유효하지_않은_페이지_사이즈(final String size) throws Exception {
        // GIVEN
        String keyword = DummyGenerator.generateNickname();
        String page = DummyGenerator.generatePage();

        RequestBuilder request = MockMvcRequestBuilders
                .get(URL.BASE_URL + URL.USER_DOMAIN)
                .param(QUERY_PARAM_KEYWORD, keyword)
                .param(QUERY_PARAM_PAGE, page)
                .param(QUERY_PARAM_SIZE, size)
                .accept(MediaType.APPLICATION_JSON);

        // WHEN and THEN
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }
}
