package com.benchpress200.photique.exhibition.api.query.controller;

import com.benchpress200.photique.common.api.constant.ApiPath;
import com.benchpress200.photique.common.api.constant.PathVariableName;
import com.benchpress200.photique.common.api.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.query.constant.ExhibitionQueryResponseMessage;
import com.benchpress200.photique.exhibition.api.query.request.ExhibitionSearchRequest;
import com.benchpress200.photique.exhibition.api.query.response.ExhibitionDetailsResponse;
import com.benchpress200.photique.exhibition.api.query.response.ExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.api.query.response.MyExhibitionSearchResponse;
import com.benchpress200.photique.exhibition.application.query.model.ExhibitionSearchQuery;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.query.port.in.SearchExhibitionUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionSearchResult;
import com.benchpress200.photique.singlework.api.query.request.MyExhibitionSearchRequest;
import com.benchpress200.photique.singlework.application.query.model.MyExhibitionSearchQuery;
import com.benchpress200.photique.singlework.application.query.port.in.SearchMyExhibitionUseCase;
import com.benchpress200.photique.singlework.application.query.result.MyExhibitionSearchResult;
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
public class ExhibitionQueryController {
    private final GetExhibitionDetailsUseCase getExhibitionDetailsUseCase;
    private final SearchExhibitionUseCase searchExhibitionUseCase;
    private final SearchMyExhibitionUseCase searchMyExhibitionUseCase;

    @GetMapping(ApiPath.EXHIBITION_DATA)
    public ResponseEntity<?> getExhibitionDetails(
            @PathVariable(PathVariableName.EXHIBITION_ID) Long exhibitionId
    ) {
        ExhibitionDetailsResult result = getExhibitionDetailsUseCase.getExhibitionDetails(exhibitionId);
        ExhibitionDetailsResponse response = ExhibitionDetailsResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.EXHIBITION_FETCH_SUCCESS,
                response
        );
    }

    @GetMapping(ApiPath.EXHIBITION_ROOT)
    public ResponseEntity<?> searchExhibition(
            @ModelAttribute @Valid ExhibitionSearchRequest request
    ) {
        ExhibitionSearchQuery query = request.toQuery();
        ExhibitionSearchResult result = searchExhibitionUseCase.searchExhibition(query);
        ExhibitionSearchResponse response = ExhibitionSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.EXHIBITION_SEARCH_SUCCESS,
                response
        );
    }

    @GetMapping(ApiPath.EXHIBITION_MY_DATA)
    public ResponseEntity<?> searchMyExhibition(
            @ModelAttribute @Valid MyExhibitionSearchRequest request
    ) {
        MyExhibitionSearchQuery query = request.toQuery();
        MyExhibitionSearchResult result = searchMyExhibitionUseCase.searchMyExhibition(query);
        MyExhibitionSearchResponse response = MyExhibitionSearchResponse.from(result);

        return ResponseHandler.handleResponse(
                HttpStatus.OK,
                ExhibitionQueryResponseMessage.MY_EXHIBITION_SEARCH_SUCCESS,
                response
        );
    }
}
