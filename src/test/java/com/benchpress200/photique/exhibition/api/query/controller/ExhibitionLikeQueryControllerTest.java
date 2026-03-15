package com.benchpress200.photique.exhibition.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchLikedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.LikedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.support.fixture.LikedExhibitionSearchResultFixture;
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

@WebMvcTest(
        controllers = ExhibitionLikeQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 좋아요 쿼리 컨트롤러 테스트")
public class ExhibitionLikeQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private SearchLikedExhibitionUseCase searchLikedExhibitionUseCase;

    @Test
    @DisplayName("좋아요 전시회 검색 요청 시 요청이 유효하면 200을 반환한다")
    public void searchLikedExhibition_whenRequestIsValid() throws Exception {
        // given
        LikedExhibitionSearchResult result = LikedExhibitionSearchResultFixture.builder().build();
        doReturn(result).when(searchLikedExhibitionUseCase).searchLikedExhibition(any());

        // when
        ResultActions resultActions = requestSearchLikedExhibition(null, null, null);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("좋아요 전시회 검색 요청 시 keyword가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidKeywords")
    public void searchLikedExhibition_whenKeywordIsInvalid(String invalidKeyword) throws Exception {
        // given
        LikedExhibitionSearchResult result = LikedExhibitionSearchResultFixture.builder().build();
        doReturn(result).when(searchLikedExhibitionUseCase).searchLikedExhibition(any());

        // when
        ResultActions resultActions = requestSearchLikedExhibition(invalidKeyword, null, null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("좋아요 전시회 검색 요청 시 page가 음수이면 400을 반환한다")
    public void searchLikedExhibition_whenPageIsNegative() throws Exception {
        // given
        LikedExhibitionSearchResult result = LikedExhibitionSearchResultFixture.builder().build();
        doReturn(result).when(searchLikedExhibitionUseCase).searchLikedExhibition(any());

        // when
        ResultActions resultActions = requestSearchLikedExhibition(null, "-1", null);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("좋아요 전시회 검색 요청 시 size가 유효하지 않으면 400을 반환한다")
    @MethodSource("invalidSizes")
    public void searchLikedExhibition_whenSizeIsInvalid(String invalidSize) throws Exception {
        // given
        LikedExhibitionSearchResult result = LikedExhibitionSearchResultFixture.builder().build();
        doReturn(result).when(searchLikedExhibitionUseCase).searchLikedExhibition(any());

        // when
        ResultActions resultActions = requestSearchLikedExhibition(null, null, invalidSize);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private static Stream<String> invalidKeywords() {
        return Stream.of(
                "한",                   // 1자 (최솟값 미만)
                "a".repeat(101)         // 101자 (최댓값 초과)
        );
    }

    private static Stream<String> invalidSizes() {
        return Stream.of(
                "0",    // 최솟값 미만
                "51"    // 최댓값 초과
        );
    }

    private ResultActions requestSearchLikedExhibition(
            String keyword,
            String page,
            String size
    ) throws Exception {
        var builder = get(ApiPath.EXHIBITION_MY_LIKE);

        if (keyword != null) {
            builder = builder.param("keyword", keyword);
        }

        if (page != null) {
            builder = builder.param("page", page);
        }

        if (size != null) {
            builder = builder.param("size", size);
        }

        return mockMvc.perform(builder);
    }
}
