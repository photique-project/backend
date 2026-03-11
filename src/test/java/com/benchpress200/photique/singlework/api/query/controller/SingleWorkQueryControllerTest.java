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
import com.benchpress200.photique.support.base.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

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

    private ResultActions requestGetSingleWorkDetails(String singleWorkId) throws Exception {
        return mockMvc.perform(
                get(ApiPath.SINGLEWORK_DATA, singleWorkId)
        );
    }
}
