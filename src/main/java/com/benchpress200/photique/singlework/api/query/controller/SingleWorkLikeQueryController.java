package com.benchpress200.photique.singlework.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.singlework.api.query.constant.SingleWorkQueryResponseMessage;
import com.benchpress200.photique.singlework.api.query.request.LikedSingleWorkSearchRequest;
import com.benchpress200.photique.singlework.api.query.response.LikedSingleWorkSearchResponse;
import com.benchpress200.photique.singlework.application.query.model.LikedSingleWorkSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.SearchLikedSingleWorkUseCase;
import com.benchpress200.photique.singlework.application.query.result.LikedSingleWorkSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SingleWorkLikeQueryController {
    private final SearchLikedSingleWorkUseCase searchLikedSingleWorkUseCase;

    @GetMapping(ApiPath.SINGLEWORK_MY_LIKE)
    public ResponseEntity<?> searchLikedSingleWork(
            @ModelAttribute @Valid LikedSingleWorkSearchRequest request
    ) {
        LikedSingleWorkSearchQuery query = request.toQuery();
        LikedSingleWorkSearchResult result = searchLikedSingleWorkUseCase.searchLikedSingleWork(query);
        LikedSingleWorkSearchResponse response = LikedSingleWorkSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                SingleWorkQueryResponseMessage.LIKED_WORK_SEARCH_SUCCESS,
                response
        );
    }
}
