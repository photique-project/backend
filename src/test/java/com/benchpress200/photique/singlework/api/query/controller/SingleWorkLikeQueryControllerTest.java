package com.benchpress200.photique.singlework.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.query.port.in.SearchLikedSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.result.LikedSingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.LikedSingleWorkSearchResultFixture;
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
        controllers = SingleWorkLikeQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("단일작품 좋아요 쿼리 컨트롤러 테스트")
public class SingleWorkLikeQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private SearchLikedSingleWorkUseCase searchLikedSingleWorkUseCase;

    @Test
    @DisplayName("좋아요한 단일작품 검색 요청 시 요청이 유효하면 200을 반환한다")
    public void searchLikedSingleWork_whenRequestIsValid() throws Exception {
        // given
        LikedSingleWorkSearchResult result = LikedSingleWorkSearchResultFixture.builder().build();
        doReturn(result).when(searchLikedSingleWorkUseCase).searchLikedSingleWork(any());

        // when
        ResultActions resultActions = requestSearchLikedSingleWork(null, 0, 10);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("좋아요한 단일작품 검색 요청 시 키워드가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidKeywords")
    public void searchLikedSingleWork_whenKeywordIsInvalid(String invalidKeyword) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchLikedSingleWork(invalidKeyword, 0, 10);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("좋아요한 단일작품 검색 요청 시 페이지 번호가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidPages")
    public void searchLikedSingleWork_whenPageIsInvalid(Integer invalidPage) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchLikedSingleWork(null, invalidPage, 10);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("좋아요한 단일작품 검색 요청 시 페이지 크기가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidSizes")
    public void searchLikedSingleWork_whenSizeIsInvalid(Integer invalidSize) throws Exception {
        // given

        // when
        ResultActions resultActions = requestSearchLikedSingleWork(null, 0, invalidSize);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidKeywords() {
        return Stream.of(
                "a",             // 1자 - 최소 길이 미만
                "a".repeat(101)  // 101자 - 최대 길이 초과
        );
    }

    private static Stream<Integer> invalidPages() {
        return Stream.of(-1);
    }

    private static Stream<Integer> invalidSizes() {
        return Stream.of(
                0,  // 최솟값 미만
                51  // 최댓값 초과
        );
    }

    private ResultActions requestSearchLikedSingleWork(String keyword, Integer page, Integer size) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_MY_LIKE);
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
