package com.benchpress200.photique.exhibition.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchBookmarkedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.BookmarkedExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.support.fixture.BookmarkedExhibitionSearchResultFixture;
import com.benchpress200.photique.support.base.BaseControllerTest;
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

@WebMvcTest(
        controllers = ExhibitionBookmarkQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 북마크 쿼리 컨트롤러 테스트")
public class ExhibitionBookmarkQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private SearchBookmarkedExhibitionUseCase searchBookmarkedExhibitionUseCase;

    @Nested
    @DisplayName("북마크 전시회 검색")
    class SearchBookmarkedExhibitionTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            BookmarkedExhibitionSearchResult result = BookmarkedExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchBookmarkedExhibitionUseCase).searchBookmarkedExhibition(any());

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibition(null, null, null);

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionBookmarkQueryControllerTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // given
            BookmarkedExhibitionSearchResult result = BookmarkedExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchBookmarkedExhibitionUseCase).searchBookmarkedExhibition(any());

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibition(invalidKeyword, null, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 유효하지 않다면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            BookmarkedExhibitionSearchResult result = BookmarkedExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchBookmarkedExhibitionUseCase).searchBookmarkedExhibition(any());

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibition(null, "-1", null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionBookmarkQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(String invalidSize) throws Exception {
            // given
            BookmarkedExhibitionSearchResult result = BookmarkedExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchBookmarkedExhibitionUseCase).searchBookmarkedExhibition(any());

            // when
            ResultActions resultActions = requestSearchBookmarkedExhibition(null, null, invalidSize);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
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

    private ResultActions requestSearchBookmarkedExhibition(
            String keyword,
            String page,
            String size
    ) throws Exception {
        var builder = get(ApiPath.EXHIBITION_MY_BOOKMARK);

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
