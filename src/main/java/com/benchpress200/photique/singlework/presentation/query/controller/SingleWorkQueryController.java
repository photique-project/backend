package com.benchpress200.photique.singlework.presentation.query.controller;

import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.constant.URL;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.singlework.application.query.model.SearchSingleWorksQuery;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkDetailsResult;
import com.benchpress200.photique.singlework.application.query.result.SingleWorkSearchResult;
import com.benchpress200.photique.singlework.application.query.service.SingleWorkQueryService;
import com.benchpress200.photique.singlework.presentation.query.constant.SingleWorkQueryResponseMessage;
import com.benchpress200.photique.singlework.presentation.query.dto.request.SingleWorkSearchRequest;
import com.benchpress200.photique.singlework.presentation.query.dto.response.SingleWorkDetailsResponse;
import com.benchpress200.photique.singlework.presentation.query.dto.response.SingleWorkSearchResponse;
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
    private final SingleWorkQueryService singleWorkQueryService;

    @GetMapping(URL.SINGLE_WORK_DATA)
    public ResponseEntity<?> getSingleWorkDetails(
            @PathVariable(PathVariableName.SINGLEWORK_ID) Long singleworkId
    ) {
        SingleWorkDetailsResult singleWorkDetailsResult = singleWorkQueryService.getSingleWorkDetails(singleworkId);
        SingleWorkDetailsResponse singleWorkDetailsResponse = SingleWorkDetailsResponse.from(singleWorkDetailsResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.WORK_FETCH_SUCCESS,
                singleWorkDetailsResponse
        );
    }

    @GetMapping
    public ResponseEntity<?> searchSingleWork(
            @ModelAttribute @Valid SingleWorkSearchRequest singleWorkSearchRequest
    ) {
        SearchSingleWorksQuery searchSingleWorksQuery = singleWorkSearchRequest.toQuery();
        SingleWorkSearchResult searchSingleWorkResult = singleWorkQueryService.searchSingleWork(
                searchSingleWorksQuery);
        SingleWorkSearchResponse searchSingleWorksResponse = SingleWorkSearchResponse.from(searchSingleWorkResult);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.WORK_SEARCH_SUCCESS,
                searchSingleWorksResponse
        );
    }
}
