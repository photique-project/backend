package com.benchpress200.photique.exhibition.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionCommentsUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionCommentsResult;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionCommentsResultFixture;
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
        controllers = ExhibitionCommentQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("전시회 감상평 쿼리 컨트롤러 테스트")
public class ExhibitionCommentQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private GetExhibitionCommentsUseCase getExhibitionCommentsUseCase;

    @Nested
    @DisplayName("전시회 감상평 페이지 조회")
    class GetExhibitionCommentsTest {
        @Test
        @DisplayName("요청이 유효하면 200을 반환한다")
        public void whenRequestValid() throws Exception {
            // given
            ExhibitionCommentsResult result = ExhibitionCommentsResultFixture.builder().build();
            doReturn(result).when(getExhibitionCommentsUseCase).getExhibitionComments(any());

            // when
            ResultActions resultActions = requestGetExhibitionComments("1", null, null);

            // then
            resultActions
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("전시회 ID가 유효하지 않다면 400을 반환한다")
        public void whenExhibitionIdInvalid() throws Exception {
            // given
            ExhibitionCommentsResult result = ExhibitionCommentsResultFixture.builder().build();
            doReturn(result).when(getExhibitionCommentsUseCase).getExhibitionComments(any());

            // when
            ResultActions resultActions = requestGetExhibitionComments("invalid", null, null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("페이지 번호가 유효하지 않다면 400을 반환한다")
        public void whenPageInvalid() throws Exception {
            // given
            ExhibitionCommentsResult result = ExhibitionCommentsResultFixture.builder().build();
            doReturn(result).when(getExhibitionCommentsUseCase).getExhibitionComments(any());

            // when
            ResultActions resultActions = requestGetExhibitionComments("1", "-1", null);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @DisplayName("페이지 사이즈가 유효하지 않으면 400을 반환한다")
        @MethodSource("com.benchpress200.photique.exhibition.api.query.controller.ExhibitionCommentQueryControllerTest#invalidSizes")
        public void whenSizeInvalid(String invalidSize) throws Exception {
            // given
            ExhibitionCommentsResult result = ExhibitionCommentsResultFixture.builder().build();
            doReturn(result).when(getExhibitionCommentsUseCase).getExhibitionComments(any());

            // when
            ResultActions resultActions = requestGetExhibitionComments("1", null, invalidSize);

            // then
            resultActions
                    .andExpect(status().isBadRequest());
        }
    }

    private static Stream<String> invalidSizes() {
        return Stream.of(
                "0",    // 최솟값 미만
                "11"    // 최댓값 초과
        );
    }

    private ResultActions requestGetExhibitionComments(
            String exhibitionId,
            String page,
            String size
    ) throws Exception {
        var builder = get(ApiPath.EXHIBITION_COMMENT, exhibitionId);

        if (page != null) {
            builder = builder.param("page", page);
        }

        if (size != null) {
            builder = builder.param("size", size);
        }

        return mockMvc.perform(builder);
    }
}
