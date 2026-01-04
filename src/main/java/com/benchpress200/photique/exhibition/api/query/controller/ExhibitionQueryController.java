package com.benchpress200.photique.exhibition.api.query.controller;

import com.benchpress200.photique.common.constant.ApiPath;
import com.benchpress200.photique.common.constant.PathVariableName;
import com.benchpress200.photique.common.response.ResponseHandler;
import com.benchpress200.photique.exhibition.api.query.constant.ExhibitionQueryResponseMessage;
import com.benchpress200.photique.exhibition.api.query.response.ExhibitionDetailsResponse;
import com.benchpress200.photique.exhibition.application.query.port.in.GetExhibitionDetailsUseCase;
import com.benchpress200.photique.exhibition.application.query.result.ExhibitionDetailsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExhibitionQueryController {
    private final GetExhibitionDetailsUseCase getExhibitionDetailsUseCase;

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
}
