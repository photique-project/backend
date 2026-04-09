package com.benchpress200.photique.exhibition.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionSearchResult;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionDetailsResultFixture;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionSearchResultFixture;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMyExhibitionUseCase;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.MyExhibitionSearchResultFixture;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(
        controllers = ExhibitionQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 쿼리 컨트롤러 테스트")
public class ExhibitionQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private GetExhibitionDetailsUseCase getExhibitionDetailsUseCase;

    @MockitoBean
    private SearchExhibitionUseCase searchExhibitionUseCase;

    @MockitoBean
    private SearchMyExhibitionUseCase searchMyExhibitionUseCase;

    @Nested
    @DisplayName("전시회 상세 조회")
    class GetExhibitionDetailsTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionDetailsResult result = ExhibitionDetailsResultFixture.builder().build();
            doReturn(result).when(getExhibitionDetailsUseCase).getExhibitionDetails(any());

            // when
            ResultActions resultActions = requestGetExhibitionDetails("1");

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("전시회 ID가 유효하지 않다면 400을 반환한다")
        public void whenExhibitionIdInvalid() throws Exception {
            // given
            ExhibitionDetailsResult result = ExhibitionDetailsResultFixture.builder().build();
            doReturn(result).when(getExhibitionDetailsUseCase).getExhibitionDetails(any());

            // when
            ResultActions resultActions = requestGetExhibitionDetails("invalid");

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("전시회 검색")
    class SearchExhibitionTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionSearchResult result = ExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchExhibitionUseCase).searchExhibition(any());

            // when
            ResultActions resultActions = requestSearchExhibition(
                    get(ApiPath.EXHIBITION_ROOT)
                            .param("target", "work")
                            .param("keyword", "기본키워드")
                            .param("page", "0")
                            .param("size", "10")
            );

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("검색 대상이 유효하지 않으면 400을 반환한다")
        public void whenTargetInvalid() throws Exception {
            // given
            ExhibitionSearchResult result = ExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchExhibitionUseCase).searchExhibition(any());

            // when
            ResultActions resultActions = requestSearchExhibition(
                    get(ApiPath.EXHIBITION_ROOT)
                            .param("target", "invalid")
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionQueryControllerTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // given
            ExhibitionSearchResult result = ExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchExhibitionUseCase).searchExhibition(any());

            // when
            ResultActions resultActions = requestSearchExhibition(
                    get(ApiPath.EXHIBITION_ROOT)
                            .param("keyword", invalidKeyword)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 유효하지 않다면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            ExhibitionSearchResult result = ExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchExhibitionUseCase).searchExhibition(any());

            // when
            ResultActions resultActions = requestSearchExhibition(
                    get(ApiPath.EXHIBITION_ROOT)
                            .param("page", "-1")
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(String invalidSize) throws Exception {
            // given
            ExhibitionSearchResult result = ExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchExhibitionUseCase).searchExhibition(any());

            // when
            ResultActions resultActions = requestSearchExhibition(
                    get(ApiPath.EXHIBITION_ROOT)
                            .param("size", invalidSize)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("내 전시회 검색")
    class SearchMyExhibitionTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            MyExhibitionSearchResult result = MyExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchMyExhibitionUseCase).searchMyExhibition(any());

            // when
            ResultActions resultActions = requestSearchMyExhibition(
                    get(ApiPath.EXHIBITION_MY_DATA)
                            .param("keyword", "기본키워드")
                            .param("page", "0")
                            .param("size", "10")
            );

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @DisplayName("키워드가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionQueryControllerTest#invalidKeywords")
        public void whenKeywordInvalid(String invalidKeyword) throws Exception {
            // given
            MyExhibitionSearchResult result = MyExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchMyExhibitionUseCase).searchMyExhibition(any());

            // when
            ResultActions resultActions = requestSearchMyExhibition(
                    get(ApiPath.EXHIBITION_MY_DATA)
                            .param("keyword", invalidKeyword)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 유효하지 않다면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            MyExhibitionSearchResult result = MyExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchMyExhibitionUseCase).searchMyExhibition(any());

            // when
            ResultActions resultActions = requestSearchMyExhibition(
                    get(ApiPath.EXHIBITION_MY_DATA)
                            .param("page", "-1")
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(String invalidSize) throws Exception {
            // given
            MyExhibitionSearchResult result = MyExhibitionSearchResultFixture.builder().build();
            doReturn(result).when(searchMyExhibitionUseCase).searchMyExhibition(any());

            // when
            ResultActions resultActions = requestSearchMyExhibition(
                    get(ApiPath.EXHIBITION_MY_DATA)
                            .param("size", invalidSize)
            );

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidKeywords() {
        return Stream.of(
                "한",                       // 1자 (최솟값 미만)
                "a".repeat(101)             // 101자 (최댓값 초과)
        );
    }

    private static Stream<String> invalidSizes() {
        return Stream.of(
                "0",    // 최솟값 미만
                "51"    // 최댓값 초과
        );
    }

    private ResultActions requestGetExhibitionDetails(String exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
        );
    }

    private ResultActions requestSearchExhibition(
            MockHttpServletRequestBuilder requestBuilder
    ) throws Exception {
        return mockMvc.perform(requestBuilder);
    }

    private ResultActions requestSearchMyExhibition(
            MockHttpServletRequestBuilder requestBuilder
    ) throws Exception {
        return mockMvc.perform(requestBuilder);
    }
}
