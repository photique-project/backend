package com.benchpress200.photique.exhibition.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.query.constant.ExhibitionQueryResponseMessage;
import com.benchpress200.photique.exhibition.api.query.request.LikedExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.api.query.response.LikedExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.application.query.model.LikedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchLikedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.LikedExhibitionSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionLikeQueryController {
    private final SearchLikedExhibitionUseCase searchLikedExhibitionUseCase;

    @GetMapping(ApiPath.EXHIBITION_MY_LIKE)
    public ResponseEntity<?> searchLikedExhibition(
            @ModelAttribute @Valid LikedExhibitionSearchRequest request
    ) {
        LikedExhibitionSearchQuery query = request.toQuery();
        LikedExhibitionSearchResult result = searchLikedExhibitionUseCase.searchLikedExhibition(query);
        LikedExhibitionSearchResponse response = LikedExhibitionSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.LIKED_EXHIBITION_SEARCH_SUCCESS,
                response
        );
    }
}
