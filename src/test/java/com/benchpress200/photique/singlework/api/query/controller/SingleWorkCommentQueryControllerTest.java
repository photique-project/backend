package com.benchpress200.photique.singlework.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkCommentsUseCase;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkCommentsResult;
import com.benchpress200.photique.singlework.application.query.support.fixture.SingleWorkCommentsResultFixture;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(
        controllers = SingleWorkCommentQueryController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@DisplayName("단일작품 댓글 쿼리 컨트롤러 테스트")
public class SingleWorkCommentQueryControllerTest extends BaseControllerTest {

    @MockitoBean
    private GetSingleWorkCommentsUseCase getSingleWorkCommentsUseCase;

    @Test
    @DisplayName("단일작품 댓글 목록 조회 요청 시 요청이 유효하면 200을 반환한다")
    public void getSingleWorkComments_whenRequestIsValid() throws Exception {
        // given
        SingleWorkCommentsResult result = SingleWorkCommentsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkCommentsUseCase).getSingleWorkComments(any());

        // when
        ResultActions resultActions = requestGetSingleWorkComments("1", 0, 5);

        // then
        resultActions
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("단일작품 댓글 목록 조회 요청 시 작품 ID가 숫자가 아니면 400을 반환한다")
    public void getSingleWorkComments_whenSingleWorkIdIsInvalid() throws Exception {
        // given
        SingleWorkCommentsResult result = SingleWorkCommentsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkCommentsUseCase).getSingleWorkComments(any());

        // when
        ResultActions resultActions = requestGetSingleWorkComments("invalid", 0, 5);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단일작품 댓글 목록 조회 요청 시 페이지가 음수이면 400을 반환한다")
    public void getSingleWorkComments_whenPageIsNegative() throws Exception {
        // given
        SingleWorkCommentsResult result = SingleWorkCommentsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkCommentsUseCase).getSingleWorkComments(any());

        // when
        ResultActions resultActions = requestGetSingleWorkComments("1", -1, 5);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    @DisplayName("단일작품 댓글 목록 조회 요청 시 사이즈가 유효하지 않으면 400을 반환한다")
    public void getSingleWorkComments_whenSizeIsInvalid(int invalidSize) throws Exception {
        // given
        SingleWorkCommentsResult result = SingleWorkCommentsResultFixture.builder().build();
        doReturn(result).when(getSingleWorkCommentsUseCase).getSingleWorkComments(any());

        // when
        ResultActions resultActions = requestGetSingleWorkComments("1", 0, invalidSize);

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestGetSingleWorkComments(
            String singleWorkId, Integer page, Integer size) throws Exception {
        MockHttpServletRequestBuilder builder = get(ApiPath.SINGLEWORK_COMMENT, singleWorkId);
        if (page != null) builder = builder.param("page", String.valueOf(page));
        if (size != null) builder = builder.param("size", String.valueOf(size));
        return mockMvc.perform(builder);
    }
}
