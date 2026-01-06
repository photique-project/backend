package com.benchpress200.photique.exhibition.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.query.constant.ExhibitionQueryResponseMessage;
import com.benchpress200.photique.exhibition.api.query.request.BookmarkedExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.api.query.response.BookmarkedExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.application.query.model.BookmarkedExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchBookmarkedExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.BookmarkedExhibitionSearchResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionBookmarkQueryController {
    private final SearchBookmarkedExhibitionUseCase searchBookmarkedExhibitionUseCase;

    @GetMapping(ApiPath.EXHIBITION_MY_BOOKMARK)
    public ResponseEntity<?> searchBookmarkedExhibition(
            @ModelAttribute @Valid BookmarkedExhibitionSearchRequest request
    ) {
        BookmarkedExhibitionSearchQuery query = request.toQuery();
        BookmarkedExhibitionSearchResult result = searchBookmarkedExhibitionUseCase.searchBookmarkedExhibition(query);
        BookmarkedExhibitionSearchResponse response = BookmarkedExhibitionSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.BOOKMARKED_EXHIBITION_SEARCH_SUCCESS,
                response
        );
    }
}
