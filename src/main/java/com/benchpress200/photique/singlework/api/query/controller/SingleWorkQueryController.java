package com.benchpress200.photique.singlework.api.query.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.query.constant.SingleWorkQueryResponseMessage;
import com.benchpress200.photique.singlework.api.query.request.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.api.query.response.SingleWorkDetailsResponse;
import com.benchpress200.photique.singlework.api.query.response.SingleWorkSearchResponse;
import com.benchpress200.photique.singlework.application.query.model.SearchSingleWorksQuery;
import com.benchpress200.photique.singlework.application.query.port.in.GetSingleWorkDetailsUseCase;
import com.benchpress200.photique.singlework.application.query.port.in.SearchSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(URL.BASE_URL + URL.SINGLE_WORK_DOMAIN)
@RequiredArgsConstructor
public class SingleWorkQueryController {
    private final GetSingleWorkDetailsUseCase getSingleWorkDetailsUseCase;
    private final SearchSingleWorkUseCase searchSingleWorkUseCase;

    @GetMapping(URL.SINGLE_WORK_DATA)
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

    @GetMapping
    public ResponseEntity<?> searchSingleWork(
            @ModelAttribute @Valid SingleWorkSearchRequest request
    ) {
        SearchSingleWorksQuery query = request.toQuery();
        SingleWorkSearchResult result = searchSingleWorkUseCase.searchSingleWork(
                query);
        SingleWorkSearchResponse response = SingleWorkSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.WORK_SEARCH_SUCCESS,
                response
        );
    }
}
