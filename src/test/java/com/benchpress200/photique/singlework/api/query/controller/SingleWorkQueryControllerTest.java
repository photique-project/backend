package com.benchpress200.photique.singlework.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.api.query.support.fixture.SingleWorkDetailsResultFixture;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMySingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.MySingleWorkSearchResultFixture;
import com.benchpress200.photique.singlework.application.query.support.fixture.SingleWorkSearchResultFixture;
import com.benchpress200.photique.support.base.BaseControllerTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
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
        controllers = SingleWorkQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("단일작품 쿼리 컨트롤러 테스트")
public class SingleWorkQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private GetSingleWorkDetailsUseCase getSingleWorkDetailsUseCase;

    @MockitoBean
    private SearchSingleWorkUseCase searchSingleWorkUseCase;

    @MockitoBean
    private SearchMySingleWorkUseCase searchMySingleWorkUseCase;

    @Test
    @DisplayName("단일작품 상세조회 요청 시 요청이 유효하면 200을 반환한다")
    public void getSingleWorkDetails_whenRequestIsValid() throws Exception {
        // given
        SingleWorkDetailsResult result = SingleWorkDetailsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkDetailsUseCase).getSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestGetSingleWorkDetails("1");

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("단일작품 상세조회 요청 시 작품 ID가 숫자가 아니면 400을 반환한다")
    public void getSingleWorkDetails_whenSingleWorkIdIsInvalid() throws Exception {
        // given
        SingleWorkDetailsResult result = SingleWorkDetailsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkDetailsUseCase).getSingleWorkDetails(any());

        // when
        ResultActions resultActions = requestGetSingleWorkDetails("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 검색 요청 시 요청이 유효하면 200을 반환한다")
    public void searchSingleWork_whenRequestIsValid() throws Exception {
        // given
        SingleWorkSearchResult result = SingleWorkSearchResultFixture.builder().build();
        doReturn(result).when(searchSingleWorkUseCase).searchSingleWork(any());

        // when
        ResultActions resultActions = requestSearchSingleWork("work", null, null, null);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("단일작품 검색 요청 시 검색 대상이 유효하지 않으면 400을 반환한다")
    public void searchSingleWork_whenTargetIsInvalid() throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchSingleWork("invalid", null, null, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 검색 요청 시 키워드가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidKeywordsForSearch")
    public void searchSingleWork_whenKeywordIsInvalid(String invalidKeyword) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchSingleWork(null, invalidKeyword, null, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 검색 요청 시 페이지 번호가 음수이면 400을 반환한다")
    public void searchSingleWork_whenPageIsNegative() throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchSingleWork(null, null, -1, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("단일작품 검색 요청 시 페이지 크기가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidSizesForSearch")
    public void searchSingleWork_whenSizeIsInvalid(Integer invalidSize) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchSingleWork(null, null, null, invalidSize);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 단일작품 검색 요청 시 요청이 유효하면 200을 반환한다")
    public void searchMySingleWork_whenRequestIsValid() throws Exception {
        // given
        MySingleWorkSearchResult result = MySingleWorkSearchResultFixture.builder().build();
        doReturn(result).when(searchMySingleWorkUseCase).searchMySingleWork(any());

        // when
        ResultActions resultActions = requestSearchMySingleWork(null, null, null);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("내 단일작품 검색 요청 시 키워드가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidKeywordsForMySingleWorkSearch")
    public void searchMySingleWork_whenKeywordIsInvalid(String invalidKeyword) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchMySingleWork(invalidKeyword, null, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 단일작품 검색 요청 시 페이지 번호가 음수이면 400을 반환한다")
    public void searchMySingleWork_whenPageIsNegative() throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchMySingleWork(null, -1, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("내 단일작품 검색 요청 시 페이지 크기가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidSizesForMySingleWorkSearch")
    public void searchMySingleWork_whenSizeIsInvalid(Integer invalidSize) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchMySingleWork(null, null, invalidSize);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidKeywordsForMySingleWorkSearch() {
        return Stream.of(
                "a",            // 1자 - 최소 길이 미만
                "a".repeat(101) // 101자 - 최대 길이 초과
        );
    }

    private static Stream<Integer> invalidSizesForMySingleWorkSearch() {
        return Stream.of(
                0,  // 최솟값 미만
                51  // 최댓값 초과
        );
    }

    private static Stream<String> invalidKeywordsForSearch() {
        return Stream.of(
                "a",            // 1자 - 최소 길이 미만
                "a".repeat(101) // 101자 - 최대 길이 초과
        );
    }

    private static Stream<Integer> invalidSizesForSearch() {
        return Stream.of(
                0,  // 최솟값 미만
                51  // 최댓값 초과
        );
    }

    private ResultActions requestSearchMySingleWork(
            String keyword, Integer page, Integer size) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_DATA);
        if (keyword != null) builder = builder.param("keyword", keyword);
        if (page != null) builder = builder.param("page", String.valueOf(page));
        if (size != null) builder = builder.param("size", String.valueOf(size));
        return mockMvc.perform(builder);
    }

    private ResultActions requestGetSingleWorkDetails(String singleWorkId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.SINGLEWORK_DATA, singleWorkId)
        );
    }

    private ResultActions requestSearchSingleWork(
            String target, String keyword, Integer page, Integer size) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_ROOT);
        if (target != null) builder = builder.param("target", target);
        if (keyword != null) builder = builder.param("keyword", keyword);
        if (page != null) builder = builder.param("page", String.valueOf(page));
        if (size != null) builder = builder.param("size", String.valueOf(size));
        return mockMvc.perform(builder);
    }
}
