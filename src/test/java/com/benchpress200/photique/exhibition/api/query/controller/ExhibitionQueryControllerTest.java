package com.benchpress200.photique.exhibition.api.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.support.fixture.ExhibitionDetailsResultFixture;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMyExhibitionUseCase;
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

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

    @Test
    @DisplayName("전시회 상세조회 요청 시 요청이 유효하면 200을 반환한다")
    public void getExhibitionDetails_whenRequestIsValid() throws Exception {
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
    @DisplayName("전시회 상세조회 요청 시 전시회 ID가 숫자가 아니면 400을 반환한다")
    public void getExhibitionDetails_whenExhibitionIdIsNotNumber() throws Exception {
        // given
        ExhibitionDetailsResult result = ExhibitionDetailsResultFixture.builder().build();
        doReturn(result).when(getExhibitionDetailsUseCase).getExhibitionDetails(any());

        // when
        ResultActions resultActions = requestGetExhibitionDetails("invalid");

        // then
        resultActions
                .andExpect(status().isBadRequest());
    }

    private ResultActions requestGetExhibitionDetails(String exhibitionId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.EXHIBITION_DATA, exhibitionId)
        );
    }
}
