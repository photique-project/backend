package com.benchpress200.photique.singlework.api.query.controller;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.query.constant.SingleWorkQueryResponseMessage;
import com.benchpress200.photique.singlework.api.query.request.MySingleWorkSearchRequest;
import com.benchpress200.photique.singlework.api.query.request.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.api.query.response.MySingleWorkSearchResponse;
import com.benchpress200.photique.singlework.api.query.response.SingleWorkDetailsResponse;
import com.benchpress200.photique.singlework.api.query.response.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.application.query.model.MySingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.model.SingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMySingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.result.MySingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SingleWorkQueryController {
    private final GetSingleWorkDetailsUseCase getSingleWorkDetailsUseCase;
    private final SearchSingleWorkUseCase searchSingleWorkUseCase;
    private final SearchMySingleWorkUseCase searchMySingleWorkUseCase;

    @GetMapping(ApiPath.SINGLEWORK_DATA)
    public ResponseEntity<?> getSingleWorkDetails(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleworkId
    ) {
        SingleWorkDetailsResult result = getSingleWorkDetailsUseCase.getSingleWorkDetails(singleworkId);
        SingleWorkDetailsResponse response = SingleWorkDetailsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.WORK_FETCH_SUCCESS,
                response
        );
    }

    @GetMapping(ApiPath.SINGLEWORK_ROOT)
    public ResponseEntity<?> searchSingleWork(
            @ModelAttribute @Valid SingleWorkSearchRequest request
    ) {
        SingleWorkSearchQuery query = request.toQuery();
        SingleWorkSearchResult result = searchSingleWorkUseCase.searchSingleWork(
                query);
        SingleWorkSearchResponse response = SingleWorkSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.WORK_SEARCH_SUCCESS,
                response
        );
    }

    @GetMapping(ApiPath.SINGLEWORK_MY_DATA)
    public ResponseEntity<?> searchMySingleWork(
            @ModelAttribute @Valid MySingleWorkSearchRequest request
    ) {
        MySingleWorkSearchQuery query = request.toQuery();
        MySingleWorkSearchResult result = searchMySingleWorkUseCase.searchMySingleWork(query);
        MySingleWorkSearchResponse response = MySingleWorkSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.MY_WORK_SEARCH_SUCCESS,
                response
        );
    }
}
